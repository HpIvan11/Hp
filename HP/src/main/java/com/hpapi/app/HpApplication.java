package com.hpapi.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HpApplication {

	public static void main(String[] args) throws IOException {
		File archivo;
		archivo = new File("src/main/resources/static/kpis.txt");
		archivo.createNewFile();

		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter("src/main/resources/static/kpis.txt");
			pw = new PrintWriter(fichero);

			pw.println("ProcessedJson:0");
			pw.println("Rows:0");
			pw.println("Calls:0");
			pw.println("Msgs:0");
			pw.println("CC-Origin:0");
			pw.println("CC-Destination:0");
			pw.print("Duraciones:0");

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

		SpringApplication.run(HpApplication.class, args);
	}
}
