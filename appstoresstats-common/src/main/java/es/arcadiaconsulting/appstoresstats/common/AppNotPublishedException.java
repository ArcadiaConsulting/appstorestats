package es.arcadiaconsulting.appstoresstats.common;

public class AppNotPublishedException extends RuntimeException {
	
	public String message;

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
		super(t);
		this.message= message;
		
	}

	public AppNotPublishedException(String message) {
		this.message=message;
	}
	
}
