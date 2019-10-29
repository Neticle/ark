package pt.neticle.ark.exceptions;

public class BusinessException extends ArkRuntimeException
{
	private final int statusCode;

	public BusinessException (int statusCode, String message)
	{
		super(message);

		this.statusCode = statusCode;
	}

	public int getStatusCode ()
	{
		return statusCode;
	}
}
