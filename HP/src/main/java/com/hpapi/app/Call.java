package com.hpapi.app;

public class Call {

	private String message_type;

	private String timestamp;

	private String origin;

	private String destination;

	private String duration;

	private String status_code;

	private String status_description;

	public Call(String message_type, String timestamp, String origin, String destination, String duration,
			String status_code, String status_description) {
		this.message_type = message_type;
		this.timestamp = timestamp;
		this.origin = origin;
		this.destination = destination;
		this.duration = duration;
		this.status_code = status_code;
		this.status_description = status_description;
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getStatus_description() {
		return status_description;
	}

	public void setStatus_description(String status_description) {
		this.status_description = status_description;
	}

	public boolean filaLlamadaFaltanCampos() {
		if (timestamp == null) {
			return true;
		}
		if (origin == null) {
			return true;
		}
		if (destination == null) {
			return true;
		}
		if (duration == null) {
			return true;
		}
		if (status_code == null) {
			return true;
		}
		if (status_description == null) {
			return true;
		}

		return false;
	}

	public boolean filaLLamadaConErrores() {
		if (timestamp.isEmpty()) {
			return true;
		}
		if (origin.isEmpty()) {
			return true;
		}
		if (destination.isEmpty()) {
			return true;
		}
		if (duration == null || duration.isEmpty()) {
			return true;
		}
		if (!(status_code.equals("OK") || status_code.equals("KO"))) {
			return true;
		}
		if (status_description.isEmpty()) {
			return true;
		}

		return false;
	}
}
