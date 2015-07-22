import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class RedditDB {
	private List<User> users;

	public RedditDB() {
		this.users = new ArrayList<User>();
	}

	public List<User> getUsers() {
		ArrayList<User> user= new ArrayList<User>();
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()){
			user.add(iter.next());			
		}
		return user;
	}

	public User addUser(String name) {
		if(name == null)
			throw new IllegalArgumentException("User name cannot be null.");
		if(findUser(name) == null){
			User newU = new User(name);
			users.add(newU);
			return newU;
		}
		else
			return null;
	}

	public User findUser(String name) {
		if(name == null)
			throw new IllegalArgumentException("User name cannot be null.");
		Iterator<User> iter = users.iterator();
		while(iter.hasNext())
		{
			User tempUser = iter.next();
			String tempName = tempUser.getName();
			if(tempName.equals(name)){
				return tempUser;
			}
		}
		return null;
	}

	private void undoPref(User user){
		List<Post> liked = user.getLiked();
		Iterator<Post> iter = liked.iterator();
		while(iter.hasNext()){
			Post tempPost = iter.next();
			tempPost.downvote();
			tempPost.downvote();
		}
		List<Post> disliked = user.getDisliked();
		Iterator<Post> iterDis = disliked.iterator();
		while(iterDis.hasNext()){
			Post tempPost = iterDis.next();
			tempPost.upvote();
			tempPost.downvote();
		}	
	}

	//remove owner's posts from liked and disliked lists!
	private void removeP(User owner){
		List<Post> posts = owner.getPosted();
		Iterator<Post> iterP = posts.iterator();
		
		while(iterP.hasNext()){
			Post tempP = iterP.next();
			Iterator<User> iterU = users.iterator(); 
			while(iterU.hasNext()){
				User tempUser2 = iterU.next();
				List<Post> liked = tempUser2.getLiked();
				if(liked.contains(tempP)){
					tempUser2.undoLike(tempP);
				}
				tempUser2.undoDislike(tempP);
			}
		}
	}
	
	public boolean delUser(String name) {
		if(name == null)
			throw new IllegalArgumentException("User name cannot be null.");
		Iterator<User> iter = users.iterator();
		while(iter.hasNext())
		{
			User tempUser = iter.next();
			String tempName = tempUser.getName();
			if(tempName.equals(name)){
				users.remove(tempUser);
				removeP(tempUser); //remove owner's posts from liked and disliked lists 
				undoPref(tempUser); //undoes likes and dislikes		
				return true;
			}
		}
		return false;
	}

	public List<Post> getFrontpage(User user) {
		if(user == null){
			List<Post> listAll = new ArrayList<Post>(); 
			Iterator<User> iterU = users.iterator();
			while(iterU.hasNext()){
				User tempU = iterU.next();
				listAll.addAll(tempU.getPosted());
			}
			return listAll;
		}
		List<String> subscribed = user.getSubscribed();
		
		List<Post> Posts = new ArrayList<Post>();
		Iterator<User> userList = users.iterator();
		List<Post> liked = user.getLiked();
		List<Post> disliked = user.getDisliked();
		//get the posts created by user him/herself
		while(userList.hasNext()){
			User tempUser = userList.next();
			List<Post> allPosts = tempUser.getPosted(); //all posts by that user
			Iterator<Post> iterPosts = allPosts.iterator();
			while(iterPosts.hasNext()){
				Post tempPost = iterPosts.next();
				if(subscribed.contains(tempPost.getSubreddit()) && (!liked.contains(tempPost) && !disliked.contains(tempPost) || tempPost.getUser().equals(user))){
					Posts.add(tempPost);
				}
			}
		}
		return Posts;
	}

	public List<Post> getFrontpage(User user, String subreddit) {
		if(subreddit == null)
			throw new IllegalArgumentException("Subreddit cannot be null.");
		if(user == null){
			List<Post> listAll = new ArrayList<Post>(); 
			Iterator<User> iterU = users.iterator();
			while(iterU.hasNext()){
				User tempU = iterU.next();
				Iterator<Post> iterP = tempU.getPosted().iterator();
				while(iterP.hasNext()){
					Post tempP = iterP.next();
					if(tempP.getSubreddit().equals(subreddit))
						listAll.add(tempP);
				}
			}
			return listAll;		
		}
		
		List<Post> liked = user.getLiked();
		List<Post> disliked = user.getDisliked();
		Iterator<User> iterU = users.iterator();
		List<Post> subP = new ArrayList<Post>(); //append to this posts list
		while(iterU.hasNext()){
			User tempU = iterU.next();
			Iterator<Post> iterP = tempU.getPosted().iterator();
			while(iterP.hasNext()){
				Post tempP = iterP.next(); 
				if(tempP.getSubreddit().equals(subreddit) && (!liked.contains(tempP) && !disliked.contains(tempP) || tempP.getUser().equals(user))){
					subP.add(tempP);
				}
			}
		}
		return subP;
	}
}