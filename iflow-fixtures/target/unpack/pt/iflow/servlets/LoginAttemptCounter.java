package pt.iflow.servlets;

import java.net.InetAddress;
import java.util.Date;

public class LoginAttemptCounter {
	
	private InetAddress addressAttempt;
	private Integer failedAttempt;
	private Date lastFailedAttempt;	
	
	public LoginAttemptCounter() {
		super();	
		failedAttempt=0;
	}

	public LoginAttemptCounter(InetAddress addressAttempt,
			Integer failedAttempt, Date lastFailedAttempt) {
		super();
		this.addressAttempt = addressAttempt;
		this.failedAttempt = failedAttempt;
		this.lastFailedAttempt = lastFailedAttempt;
	}

	public InetAddress getAddressAttempt() {
		return addressAttempt;
	}

	public void setAddressAttempt(InetAddress addressAttempt) {
		this.addressAttempt = addressAttempt;
	}

	public Integer getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(Integer failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public Date getLastFailedAttempt() {
		return lastFailedAttempt;
	}

	public void setLastFailedAttempt(Date lastFailedAttempt) {
		this.lastFailedAttempt = lastFailedAttempt;
	}

}
