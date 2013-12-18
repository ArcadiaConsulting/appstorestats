package es.arcadiaconsulting.appstoresstats.common;

public class AppNotPublishedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppNotPublishedException(Throwable t)
	{
		super(t);
	}
	
	public AppNotPublishedException(String message,Throwable t)
	{
		super(message,t);
	}
	
}
