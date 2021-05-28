package com.hpapi.app;

public class Msg {

	private String message_type;

	private String timestamp;

	private String origin;

	private String destination;

	private String message_content;

	private String message_status;

	public Msg(String message_type, String timestamp, String origin, String destination, String message_content,
			String message_status) {
		this.message_type = message_type;
		this.timestamp = timestamp;
		this.origin = origin;
		this.destination = destination;
		this.message_content = message_content;
		this.message_status = message_status;
	}

	public String getMessage_type() {
		return message_type;
	}

	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getMessage_content() {
		return message_content;
	}

	public void setMessage_content(String message_content) {
		this.message_content = message_content;
	}

	public String getMessage_status() {
		return message_status;
	}

	public void setMessage_status(String message_status) {
		this.message_status = message_status;
	}

	public boolean filaMensajeFaltanCampos() {

		if (timestamp == null)
			return true;
		if (origin == null)
			return true;
		if (destination == null)
			return true;
		if (message_content == null)
			return true;
		if (message_status == null)
			return true;

		return false;
	}

	public boolean filaMensajeConErrores() {

		if (timestamp.isEmpty())
			return true;
		if (origin.isEmpty())
			return true;
		if (destination.isEmpty())
			return true;
		if (!(message_status.equals("DELIVERED") || message_status.equals("SEEN")))
			return true;

		return false;
	}

	public int mensajesEnBlanco() {
		int blanco = 0;
		if (message_content != null && message_content.isEmpty()) {
			blanco++;
		}
		return blanco;
	}
}
