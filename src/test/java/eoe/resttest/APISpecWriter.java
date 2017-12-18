package eoe.resttest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class APISpecWriter {
	
	public static void main(String[] args) {
		APISpecWriter aPISpecWriter = new APISpecWriter();
		
		
		
	}
	
	public APISpecInfo extractSpecInfo(String testClassFqcn) throws ReflectiveOperationException{ 
		APISpecInfo apiSpecInfo = new APISpecInfo();
		Class<EoEAPITestCase> aClazz = (Class<EoEAPITestCase>) Class.forName(testClassFqcn);
		Method  method = aClazz.getDeclaredMethod ("method name");
		
		Object aTarget = aClazz.newInstance();
		method.invoke (aTarget);
		return apiSpecInfo;
	}
	
	public void writeIntoHtml() {
		
	}
	
	public void writeIntoTxt() {
		
	}
	
	public void writeIntoExcel() {
		
	}

}

class APISpecInfo{
	
}