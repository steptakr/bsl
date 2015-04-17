package nl.debijenkorf.bsl.core;

import nl.debijenkorf.bsl.exceptions.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Parameterized.class)
public class ExceptionControllerShould {

    private static String defaultErrorMessage = "An error has occurred.";
    private static HttpStatus defaultHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    private final String endPoint;
    private final String expectedErrorMessage;
    private final HttpStatus expectedHttpStatus;
    private MockMvc mockMvc;

    @InjectMocks
    private ExceptionController exceptionController;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"/exception/throw1", ArgumentNullException.class.getAnnotation(ResponseStatus.class).reason(), ArgumentNullException.class.getAnnotation(ResponseStatus.class).value()},
                {"/exception/throw2", defaultErrorMessage, defaultHttpStatus},
                {"/exception/throw3", "Locale is not defined. EN-gb, TR-tr etc.", HttpStatus.BAD_REQUEST},
                {"/exception/throw4", defaultErrorMessage, defaultHttpStatus},
                {"/exception/throw5", "This was an example BSL exception message.", HttpStatus.FAILED_DEPENDENCY},
                {"/exception/throw6", defaultErrorMessage, HttpStatus.NOT_ACCEPTABLE},
                {"/exception/throw7", "This message is from the api endpoint exception not the handler.", HttpStatus.NOT_ACCEPTABLE},
                {"/exception/throw8", ExampleBSLExceptionWithResponseStatusAnnotation.class.getAnnotation(ResponseStatus.class).reason(), ExampleBSLExceptionWithResponseStatusAnnotation.class.getAnnotation(ResponseStatus.class).value()},
        });
    }

    public ExceptionControllerShould(String endPoint, String expectedErrorMessage, HttpStatus expectedHttpStatus){

        this.endPoint = endPoint;
        this.expectedErrorMessage = expectedErrorMessage;
        this.expectedHttpStatus = expectedHttpStatus;
    }


    @Before
    public void SetUp(){

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(exceptionController).setHandlerExceptionResolvers(createExceptionResolver()).build();
    }

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(BSLResponseEntityExceptionHandler.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new BSLResponseEntityExceptionHandler(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    @Test
    public void return_Appropriate_Error_Responses() throws Exception {
        MvcResult result = mockMvc.perform(get(endPoint))
                .andExpect(status().is(this.expectedHttpStatus.value()))
                .andReturn();

        String errorMessage = result.getResponse().getContentAsString();

        assertEquals(this.expectedErrorMessage, errorMessage);
    }
}

@Controller("")
@RequestMapping("/exception")
class ExceptionController {
    @RequestMapping(value = "/throw1", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowArgumentNullException() {
        throw new ArgumentNullException("this is an example ArgumentNullException.");
    }

    @RequestMapping(value = "/throw2", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowIOException() throws IOException {
        throw new IOException("this is an example IOException.");
    }

    @RequestMapping(value = "/throw4", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowNullPointerException() {
        throw new NullPointerException("this is an example NullPointerException.");
    }

    @RequestMapping(value = "/throw5", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowExampleBSLException() {
        throw new ExampleBSLException();
    }

    @RequestMapping(value = "/throw6", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowExampleBSLExceptionWithoutOverriddenResponseBody() {
        throw new ExampleBSLExceptionWithoutOverriddenResponseBody();
    }

    @RequestMapping(value = "/throw7", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowExampleBSLExceptionWithoutOverriddenResponseBodyWithMessage() {
        throw new ExampleBSLExceptionWithoutOverriddenResponseBody("This message is from the api endpoint exception not the handler.");
    }

    @RequestMapping(value = "/throw8", method = RequestMethod.GET)
    @ResponseBody
    public String ThrowExampleBSLExceptionWithResponseStatusAnnotation() {
        throw new ExampleBSLExceptionWithResponseStatusAnnotation();
    }
}

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "This is a good reason.")
class ExampleBSLExceptionWithResponseStatusAnnotation extends BSLException {

}

class ExampleBSLExceptionWithoutOverriddenResponseBody extends BSLException {

    public ExampleBSLExceptionWithoutOverriddenResponseBody(){ super(); }

    public ExampleBSLExceptionWithoutOverriddenResponseBody(String message) {
        super(message);
    }

    @Override
    protected HttpStatus getStatusCode() {
        return HttpStatus.NOT_ACCEPTABLE;
    }
}


class ExampleBSLException extends BSLException {

    public ExampleBSLException(){}

    public ExampleBSLException(String message) {
        super(message);
    }

    protected ExampleBSLException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected HttpStatus getStatusCode() {
        return HttpStatus.FAILED_DEPENDENCY;
    }

    @Override
    protected String getResponseBody() {
        return "This was an example BSL exception message.";
    }
}


