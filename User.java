import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class User {
	final private String name;
	final private Karma karma;
	private List<String> subscribed;
	private List<Post> posted;
	private List<Post> liked;
	private List<Post> disliked;

	public User(String name) {
		this.name = name;
		this.karma = new Karma();
		this.subscribed = new ArrayList<String>(); 
		this.posted = new ArrayList<Post>();
		this.liked = new ArrayList<Post>();
		this.disliked = new ArrayList<Post>();
	}

	public String getName() {
		return this.name;
	}

	public Karma getKarma() {
		return this.karma;
	}

	public List<String> getSubscribed() {
		List<String> copy = new ArrayList<String>();
		Iterator<String> iter = subscribed.iterator();
		while(iter.hasNext()){
			copy.add(iter.next());
		}
		return copy;
	}

	public List<Post> getPosted() {
		List<Post> copy = new ArrayList<Post>();
		Iterator<Post> iter = posted.iterator();
		while(iter.hasNext()){
			copy.add(iter.next());
		}
		return copy;
	}

	public List<Post> getLiked() {
		List<Post> copy = new ArrayList<Post>();
		Iterator<Post> iter = liked.iterator();
		while(iter.hasNext()){
			copy.add(iter.next());
		}
		return copy;
	}

	public List<Post> getDisliked() {
		List<Post> copy = new ArrayList<Post>();
		Iterator<Post> iter = disliked.iterator();
		while(iter.hasNext()){
			copy.add(iter.next());
		}
		return copy;
	}

	public void subscribe(String subreddit) {
		if(subreddit == null)
			throw new IllegalArgumentException("Subreddit cannot be null.");
		if(!subscribed.contains(subreddit))
			subscribed.add(subreddit);
		else
			subscribed.remove(subreddit);
	}

	public void unsubscribe(String subreddit) {
		if(subreddit == null)
			throw new IllegalArgumentException("Subreddit cannot be null.");
		if(subscribed.contains(subreddit))
			subscribed.remove(subreddit);
	}

	public Post addPost(String subreddit, PostType type, String title) {
		if(subreddit == null || type == null || title == null)
			throw new IllegalArgumentException("null argument not allowed!.");
		Post newPost = new Post(this, subreddit, type, title);
		posted.add(newPost);
		return newPost;
	}

	public void like(Post post) {
		if(post == null)
			throw new IllegalArgumentException("Post cannot be null.");
		if(!liked.contains(post)){
			liked.add(post);
			post.upvote();
		}
		else{
			undoLike(post);
			}
		if(disliked.contains(post))
			undoDislike(post);
	}

	public void undoLike(Post post) {
		if(post == null)
			throw new IllegalArgumentException("Post cannot be null.");
		if(liked.contains(post))
		{
			liked.remove(post);
			post.downvote();
			post.downvote();
		}
	}

	public void dislike(Post post) {
		if(post == null)
			throw new IllegalArgumentException("Post cannot be null.");
		if(!disliked.contains(post)){
			disliked.add(post);
			post.downvote();
		}
		else
			undoDislike(post);
		if(liked.contains(post))
			undoLike(post);
	}

	public void undoDislike(Post post) {
		if(post == null)
			throw new IllegalArgumentException("Post cannot be null.");
		if(disliked.contains(post))
		{
			disliked.remove(post);
			post.upvote();
			post.downvote();
		}
	}
}