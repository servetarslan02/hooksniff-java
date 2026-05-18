package com.hooksniff.exceptions;

/**
 * Base exception for all HookSniff API errors.
 */
public class HookSniffApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;
    private final java.util.Map<String, String> headers;

    public HookSniffApiException(String message, int statusCode, String responseBody, java.util.Map<String, String> headers) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.headers = headers != null ? headers : java.util.Collections.emptyMap();
    }

    public HookSniffApiException(String message, int statusCode, String responseBody) {
        this(message, statusCode, responseBody, java.util.Collections.emptyMap());
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
    public java.util.Map<String, String> getHeaders() { return headers; }
}

/** 400 Bad Request — The request was malformed or missing required fields */
class BadRequestException extends HookSniffApiException {
    public BadRequestException(String detail, java.util.Map<String, String> headers) {
        super(detail != null ? detail : "Bad request", 400, detail, headers);
    }
}

/** 401 Unauthorized — Invalid or missing authentication */
class UnauthorizedException extends HookSniffApiException {
    public UnauthorizedException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Unauthorized", 401, message, headers);
    }
}

/** 403 Forbidden — Insufficient permissions */
class ForbiddenException extends HookSniffApiException {
    public ForbiddenException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Forbidden", 403, message, headers);
    }
}

/** 404 Not Found — Resource does not exist */
class NotFoundException extends HookSniffApiException {
    public NotFoundException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Not found", 404, message, headers);
    }
}

/** 409 Conflict — Resource already exists or conflict */
class ConflictException extends HookSniffApiException {
    public ConflictException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Conflict", 409, message, headers);
    }
}

/** 422 Unprocessable Entity — Validation error */
class UnprocessableEntityException extends HookSniffApiException {
    private final java.util.List<ValidationErrorItem> validationErrors;

    public UnprocessableEntityException(java.util.List<ValidationErrorItem> validationErrors, String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Unprocessable entity", 422, message, headers);
        this.validationErrors = validationErrors != null ? validationErrors : java.util.Collections.emptyList();
    }

    public java.util.List<ValidationErrorItem> getValidationErrors() { return validationErrors; }
}

/** 429 Too Many Requests — Rate limit exceeded */
class RateLimitException extends HookSniffApiException {
    private final Integer retryAfter;

    public RateLimitException(Integer retryAfter, java.util.Map<String, String> headers) {
        super("Rate limit exceeded" + (retryAfter != null ? " (retry after " + retryAfter + "s)" : ""), 429, null, headers);
        this.retryAfter = retryAfter;
    }

    public Integer getRetryAfter() { return retryAfter; }
}

/** 500 Internal Server Error */
class InternalServerException extends HookSniffApiException {
    public InternalServerException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Internal server error", 500, message, headers);
    }
}

/** 502 Bad Gateway */
class BadGatewayException extends HookSniffApiException {
    public BadGatewayException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Bad gateway", 502, message, headers);
    }
}

/** 503 Service Unavailable */
class ServiceUnavailableException extends HookSniffApiException {
    public ServiceUnavailableException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Service unavailable", 503, message, headers);
    }
}

/** 504 Gateway Timeout */
class GatewayTimeoutException extends HookSniffApiException {
    public GatewayTimeoutException(String message, java.util.Map<String, String> headers) {
        super(message != null ? message : "Gateway timeout", 504, message, headers);
    }
}

/** Validation error item from 422 responses */
class ValidationErrorItem {
    private final String[] loc;
    private final String msg;
    private final String type;

    public ValidationErrorItem(String[] loc, String msg, String type) {
        this.loc = loc;
        this.msg = msg;
        this.type = type;
    }

    public String[] getLoc() { return loc; }
    public String getMsg() { return msg; }
    public String getType() { return type; }
}

/** Factory to create the appropriate exception from a status code */
class HookSniffApiExceptionFactory {
    public static HookSniffApiException create(int statusCode, String body, java.util.Map<String, String> headers) {
        switch (statusCode) {
            case 400: return new BadRequestException(body, headers);
            case 401: return new UnauthorizedException(body, headers);
            case 403: return new ForbiddenException(body, headers);
            case 404: return new NotFoundException(body, headers);
            case 409: return new ConflictException(body, headers);
            case 422: return new UnprocessableEntityException(null, body, headers);
            case 429:
                Integer retryAfter = headers.containsKey("retry-after")
                    ? Integer.parseInt(headers.get("retry-after"))
                    : null;
                return new RateLimitException(retryAfter, headers);
            case 500: return new InternalServerException(body, headers);
            case 502: return new BadGatewayException(body, headers);
            case 503: return new ServiceUnavailableException(body, headers);
            case 504: return new GatewayTimeoutException(body, headers);
            default: return new HookSniffApiException("HTTP " + statusCode, statusCode, body, headers);
        }
    }
}
