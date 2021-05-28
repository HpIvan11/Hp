package com.hpapi.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class HPController {

	@GetMapping("/date/{date}")
	public String findProductById(@PathVariable String date) {

		HashMap<String, String> countryCodes = new HashMap();

		countryCodes.put("34", "Spain");
		countryCodes.put("49", "Germany");
		countryCodes.put("44", "England");

		HashMap<String, Integer> countryCodesCounter = new HashMap();

		countryCodesCounter.put("34", 0);
		countryCodesCounter.put("49", 0);
		countryCodesCounter.put("44", 0);

		HashMap<String, ArrayList<Integer>> averageCallDurationByCountryCodes = new HashMap();

		averageCallDurationByCountryCodes.put("34", new ArrayList<Integer>());
		averageCallDurationByCountryCodes.put("49", new ArrayList<Integer>());
		averageCallDurationByCountryCodes.put("44", new ArrayList<Integer>());

		HashMap<String, Integer> messageOcurrence = new HashMap();

		int filasCamposFaltan = 0;
		int filasErrores = 0;
		int mensajesEnBlanco = 0;
		int okCalls = 0;
		int koCalls = 0;
		int totalRows = 0;
		int totalCalls = 0;
		int totalMessages = 0;
		int differentCountryCodes = 0;

		if (date.matches("[0-9]{8}")) {
			long startTime = System.currentTimeMillis();
			try {
				// Se abre la conexión
				URL url = new URL("https://raw.githubusercontent.com/vas-test/test1/master/logs/MCP_" + date + ".json");
				URLConnection conexion = url.openConnection();
				conexion.connect();

				// Lectura
				InputStream is = conexion.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String linea;
				while ((linea = br.readLine()) != null) {
					totalRows++;
					try {
						Gson gson = new Gson();
						if (linea.contains("message_type")) {
							if (linea.contains("CALL")) {
								totalCalls++;
								Call llamada = gson.fromJson(linea, Call.class);
								if (llamada.filaLlamadaFaltanCampos()) {
									filasCamposFaltan++;
								} else {
									if (llamada.filaLLamadaConErrores()) {
										filasErrores++;
									}
								}
								if (llamada.getOrigin() != null) {
									String countryCode = llamada.getOrigin().substring(0, 2);
									Integer a = countryCodesCounter.get(countryCode);
									countryCodesCounter.put(countryCode, ++a);
								}
								if (llamada.getStatus_code() != null) {
									if (llamada.getStatus_code().equals("OK")) {
										okCalls++;
									}
									if (llamada.getStatus_code().equals("KO")) {
										koCalls++;
									}

									if (llamada.getDuration() != null) {
										int duracion = Integer.parseInt(llamada.getDuration());
										if (llamada.getOrigin() != null) {
											String countryCode = llamada.getOrigin().substring(0, 2);
											ArrayList<Integer> lista = averageCallDurationByCountryCodes
													.get(countryCode);
											lista.add(duracion);
											averageCallDurationByCountryCodes.put(countryCode, lista);
										}
									}
								}

							} else if (linea.contains("MSG")) {
								totalMessages++;
								Msg mensaje = gson.fromJson(linea, Msg.class);
								if (mensaje.filaMensajeFaltanCampos()) {
									filasCamposFaltan++;
								} else {
									if (mensaje.filaMensajeConErrores()) {
										filasErrores++;
									}
								}
								mensajesEnBlanco += mensaje.mensajesEnBlanco();
								if (mensaje.getMessage_content() != null && !mensaje.getMessage_content().isEmpty()) {
									String[] palabras = mensaje.getMessage_content().split(" ");
									for (int i = 1; i < palabras.length; i++) {
										if (!messageOcurrence.containsKey(palabras[i])) {
											messageOcurrence.put(palabras[i], 1);
										} else {
											Integer a = messageOcurrence.get(palabras[i]);
											messageOcurrence.put(palabras[i], ++a);
										}
									}
								}
							} else {
								System.out.println("Fallo");
								filasErrores++;
							}
						} else {
							filasCamposFaltan++;
						}
					} catch (Exception ex) {
						System.out.println("Ha habido un error");
					}
				}
				long endTime = System.currentTimeMillis() - startTime;

				escribirFichero(filasCamposFaltan, mensajesEnBlanco, filasErrores, countryCodesCounter, okCalls,
						koCalls, averageCallDurationByCountryCodes, messageOcurrence, countryCodes);

				escribirKpis(totalRows, totalCalls, totalMessages, countryCodesCounter, endTime);

				// System.out.println(datos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				return "Json no encontrado";
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Día especificado no válido");
			return "Día especificado no válido";
		}
		return "Json Processed";
	}

	@GetMapping("/metrics")
	public String metricas() {
		return leerFichero();
	}

	@GetMapping("/kpis")
	public String total() {
		return leerFichero2();
	}

	public static int average(ArrayList<Integer> lista) {
		int suma = 0;
		if (!lista.isEmpty()) {
			for (Integer a : lista) {
				suma += a;
			}
			return suma / lista.size();
		}
		return 0;
	}

	public static void escribirFichero(int camposQueFaltan, int mensajesEnBlanco, int camposConErrores,
			HashMap<String, Integer> countryCodesCounter, int okCalls, int koCalls,
			HashMap<String, ArrayList<Integer>> averageCallDurationByCountryCodes,
			HashMap<String, Integer> messageOcurrence, HashMap<String, String> countryCodes) {
		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter("src/main/resources/static/ultimoProcesado.txt");
			pw = new PrintWriter(fichero);

			pw.println("Líneas con campos que faltan: " + camposQueFaltan);
			pw.println("Mensajes en blanco: " + mensajesEnBlanco);
			pw.println("Errores: " + camposConErrores);
			for (String key : countryCodesCounter.keySet()) {
				pw.println("Llamadas de " + countryCodes.get(key) + "(" + key + "): " + countryCodesCounter.get(key));
			}
			pw.println("Llamadas OK:" + okCalls);
			pw.println("Llamadas KO:" + koCalls);
			for (String key : averageCallDurationByCountryCodes.keySet()) {
				pw.println("Media de " + key + "(" + countryCodes.get(key) + "): "
						+ average(averageCallDurationByCountryCodes.get(key)));
			}
			pw.println("Ocurrencia de palabras");
			for (String key : messageOcurrence.keySet()) {
				pw.println(key + ": " + messageOcurrence.get(key));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void escribirKpis(int rows, int calls, int messages, HashMap<String, Integer> countryCodesCounter,
			long duracion) {

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		ArrayList<String> lectura = new ArrayList<String>();
		ArrayList<String> duraciones = new ArrayList<String>();

		try {
			archivo = new File("src/main/resources/static/kpis.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String linea;
			while ((linea = br.readLine()) != null)
				lectura.add(linea);

		} catch (Exception e) {

		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			FileWriter fichero = null;
			PrintWriter pw = null;
			try {
				fichero = new FileWriter("src/main/resources/static/kpis.txt");
				pw = new PrintWriter(fichero);
				int procesados = Integer.parseInt(lectura.get(0).split(":")[1]) + 1;
				pw.println("ProcessedJson:" + procesados);
				int filas = Integer.parseInt(lectura.get(1).split(":")[1]) + rows;
				pw.println("Rows:" + filas);
				int llamadas = Integer.parseInt(lectura.get(2).split(":")[1]) + calls;
				pw.println("Calls:" + llamadas);
				int mensajes = Integer.parseInt(lectura.get(3).split(":")[1]) + messages;
				pw.println("Msgs:" + mensajes);
				pw.println("CC-Origin:" + countryCodesCounter.keySet().size());
				pw.println("CC-Destination:" + countryCodesCounter.keySet().size());
				if (lectura.get(6).contains("0")) {
					pw.print("Duraciones:" + duracion + " ms");
				} else {
					pw.print("Duraciones:" + lectura.get(6).split(":")[1] + ", " + duracion + " ms");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// Nuevamente aprovechamos el finally para
					// asegurarnos que se cierra el fichero.
					if (null != fichero)
						fichero.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	public static String leerFichero() {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File("src/main/resources/static/ultimoProcesado.txt");
			if (archivo.exists()) {
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);

				// Lectura del fichero
				String lectura = "";
				String linea;
				while ((linea = br.readLine()) != null)
					lectura = lectura + linea + "\n";
				return lectura;
			} else {
				return "No se ha procesado ningún JSON";
			}
		} catch (Exception e) {
			return "Fallo en la lectura";
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static String leerFichero2() {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File("src/main/resources/static/kpis.txt");
			if (archivo.exists()) {
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);

				// Lectura del fichero
				String lectura = "";
				String linea;
				while ((linea = br.readLine()) != null)
					lectura = lectura + linea + "\n";
				return lectura;
			} else {
				return "No se ha procesado ningún JSON";
			}
		} catch (Exception e) {
			return "Fallo en la lectura";
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
