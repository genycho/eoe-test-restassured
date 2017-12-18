package eoe.resttest;

public class APIPathDecl {
	/*	APP MANAGER	*/
	public static final String APIPATH_CREATEAPPMANAGER = "/api/tenants/register";
	public static final String APIPATH_DELETEAPPMANAGER = "/api/tenants/{id} ";
	public static final String APIPATH_SEARCHAPPMANAGER = "/api/tenants/search/by-account";
	
	/*	APPLICATION	*/
	public static final String APIPATH_CREATEAPPLICATION = "/api/applications/own";
	public static final String APIPATH_SEARCHAPPLICATION = "/api/applications/search/by-name-like";
	public static final String APIPATH_DELETEAPPLICATION = "/api/applications/own/{id}";
	public static final String APIPATH_CREATEENDUSER = "/api/end-users/register";
	public static final String APIPATH_UPDATEAPPLICATION = "/api/applications/own/{id}";
	public static final String APIPATH_GETENDUSER = "/api/end-users/{id}";
	public static final String APIPATH_SEARCHENDUSEREMAIL = "/api/end-users/search/by-email";
	public static final String APIPATH_DELETEENDUSER = "/api/end-users/{id}" ;
	public static final String APIPATH_ENDUSERLOGIN = "/oauth/token";
	public static final String APIPATH_GETAPPLICATIONLIST = "/api/applications/own";
	
	public static final String APIPATH_GETAPPLICATION ="/api/applications/own/{id}";
	
	
	
}
