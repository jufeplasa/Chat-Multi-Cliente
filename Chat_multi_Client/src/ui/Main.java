package ui;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String messa="-felipe a b c d e";
		String a=extractInfo(messa,1);
		String me=extractInfo(messa,2);
		System.out.println(a);
		System.out.println(me);
	}
	
	public static String extractInfo(String message, int option) {
		String info="";
		if(option==1) {
	    	String temp=message.split(" ")[0];
	    	info = temp.split("-")[1];
		}
		else if (option==2) {
			info=message.split(" ", 2)[1];
		}
    	
    	return info;
    }
	
	

}
