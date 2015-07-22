import java.util.Iterator;
import java.io.*;
import java.util.List;
//import java.util.ArrayList;

import java.util.Scanner;

public class Reddit {
	private RedditDB db;
	
	public Reddit(){
		db = new RedditDB();
	}
	
	public static void showSum(Reddit rd){
		List<User> users = rd.db.getUsers();
		Iterator<User> iterU = users.iterator();
		while(iterU.hasNext()){
			User tempU = iterU.next();
			Karma tempK = tempU.getKarma();
			System.out.println(tempU.getName() + "\t" + tempK.getLinkKarma() + "\t" + tempK.getCommentKarma());
		}
	}
	
	public static void display(Reddit rd, boolean status, String login, String name, boolean user, Scanner scanner){ //true if username
		Scanner disp = scanner;
		String command;
		boolean valid = false; //valid command or not
		List<Post> frontP;
		//get the front page 
		if(user == true){
			frontP = rd.db.findUser(name).getPosted(); //u, name here is name of user specified
		}
		else if(name.equals("temptemp")){ //f
			if(status)
				frontP = rd.db.getFrontpage(rd.db.findUser(login)); //subreddits subscribed by certain user
			else
				frontP = rd.db.getFrontpage(null); 
		}
		else
			frontP = rd.db.getFrontpage(null, name); //r, all posts in the particular subreddit "name"
		boolean first = true;
		Iterator<Post> iterPosts = frontP.iterator();
		if(!iterPosts.hasNext()){
			System.out.println("No posts left to display.");
			System.out.println("Exiting to the main menu...");
			return;
		}
		Post tempPost = iterPosts.next();
		
		while(iterPosts.hasNext() || first){	
			first = false;
			if(valid)
				tempPost = iterPosts.next();
			System.out.println(tempPost.getKarma() + "\t" + tempPost.getTitle());
			valid = true;
			if(status){
				System.out.print("["+ login + "@reddit]$ ");
			}else
				System.out.print("[anon@reddit]$ ");
			String[] longCommand = disp.nextLine().split("\\s");
			command = longCommand[0];
			if(longCommand.length > 1){
				System.out.println("Invalid command!");
				valid = false;
			}
			else if(command.equals("a")){
				if(!status){
					System.out.println("Login to like post.");
					valid = false;
				}
				else
				{
					rd.db.findUser(login).like(tempPost);
				}
			}else if(command.equals("z")){
				if(!status){
					System.out.println("Login to dislike post.");
					valid = false;
				}
				else
				{
					rd.db.findUser(login).dislike(tempPost);
				}
			}else if(command.equals("j")){
				continue;
			}else if(command.equals("x")){
				System.out.println("Exiting to the main menu...");
				return;
			}else{
				System.out.println("Invalid command!");	
				valid = false;
			}
		}
		System.out.println("No posts left to display.");
		System.out.println("Exiting to the main menu...");
	}

	public static void main(String[] args) throws FileNotFoundException{
		if(args.length == 0){
			System.out.println("Usage: java Reddit <FileNames>");
			return;
		}
		
		Reddit rd = new Reddit();
		if(rd.db.addUser("admin") == null)
			System.out.println("admin not created.");
		
		for(int i=0; i<args.length; i++){
			File tempFile = new File(args[i]);
			if(!tempFile.exists()){
				System.out.println("File " + args[i] + " not found.");
				return;
			}
			Scanner fileSc = new Scanner(tempFile); 
			String[] tempName = args[i].split("\\.");
			User newUser = rd.db.addUser(tempName[0]); //add user name
			
			if(fileSc.hasNextLine()){			
				String[] firstLine = fileSc.nextLine().toLowerCase().split(", "); //convert to lower case
				for(int j = 0; j < firstLine.length; j++){ //subreddits
					newUser.subscribe(firstLine[j]);
				}
			}
			while(fileSc.hasNextLine()){//parse rest of file				
				String[] nextLine = fileSc.nextLine().split(", ");
				if(nextLine.length < 3){
					fileSc.close();
					throw new IllegalArgumentException("User information incorrect format.");				
				}
				PostType tempType = PostType.valueOf(nextLine[1]);
				String title = nextLine[2];
				for(int k = 3; k < nextLine.length; k++){
					title += ", ";
					title += nextLine[k];
				}
				Post newPost = newUser.addPost(nextLine[0], tempType, title);
				newUser.like(newPost);
			}
			fileSc.close();
		}
		
		Scanner scanner = new Scanner(System.in);
		boolean status = false; //whether logged in
		String login = " "; //name of person logged in
		while(true){			
			if(status)
				System.out.print("["+ login + "@reddit]$ ");
			else
				System.out.print("[anon@reddit]$ ");	
			String[] longCommand = scanner.nextLine().split("\\s");
			String command = longCommand[0];
			if(command.equals("s")){
				if(status && login.equals("admin")){
					showSum(rd); //show summary of all users
				}else
					System.out.println("Invalid command!");
			}else if(command.equals("d")){ //only first char!
				if(status && login.equals("admin")){
					String userName = longCommand[1];
					User tempU = rd.db.findUser(userName);
					if(tempU == null)
						System.out.println("User "+ userName + " not found.");
					else
						{
							rd.db.delUser(userName); //check return
							System.out.println("User " +userName + " deleted.");							
						}
				}else
					System.out.println("Invalid command!");
				
			}else if(command.equals("l")){
				if(status && longCommand.length > 1){
					System.out.println("User " + login + " already logged in.");
				}else if(longCommand.length == 1){
					if(!status)
						System.out.println("No user logged in.");
					else{
						status = false;
						System.out.println("User " + login + " logged out.");
						login = " "; //clear login, not necessary
					}
				}else if(longCommand.length > 1){
					String tempName = longCommand[1];
					if(rd.db.findUser(tempName) == null){
						System.out.println("User " + tempName + " not found.");
					}else{
						login = tempName;
						status = true;
						System.out.println("User "+ login + " logged in.");
					}
				}
			}else if(command.equals("f")){
				if(longCommand.length > 1){
					System.out.println("Invalid command!");
					continue;
				}
				System.out.println("Displaying the front page...");
				display(rd, status, login, "temptemp", false, scanner); //ignore the false for this
				continue;
			}else if(command.equals("r")){
				if(longCommand.length == 1){
					System.out.println("Invalid command!");
					continue;
				}
				String redditName = longCommand[1];
				if (redditName.equals(null))
					System.out.println("Invalid command!");
				else{
					System.out.println("Displaying /r/"+ redditName +"...");
					display(rd, status, login, redditName, false, scanner);
				}
				continue;
			}else if(command.equals("u")){				
				if (longCommand.length == 1){
					System.out.println("Invalid command!");
					continue;
				}
				else{
					String userName = longCommand[1];
					System.out.println("Displaying /u/"+ userName +"...");
					display(rd, status, login, userName, true, scanner);
				}
			}else if(command.equals("x")){
				System.out.println("Exiting to the real world...");
				break;		
			}else
				System.out.println("Invalid command!");
			//scanner.nextLine();		
		}
		scanner.close();
	
	}
}
