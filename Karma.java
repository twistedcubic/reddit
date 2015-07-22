public class Karma {
	private int linkKarma;
	private int commentKarma;

	public Karma() {
		this.linkKarma = 0;
		this.commentKarma = 0;
	}

	public void upvote(PostType type) {
		switch (type){
		case COMMENT:
			commentKarma += 2;
			break;
		case LINK:
			linkKarma += 2;
			break;
		case SELF:
			break;
		}
	}

	public void downvote(PostType type) {
		switch (type){
		case COMMENT:
			commentKarma -= 1;
			break;
		case LINK:
			linkKarma -= 1;
			break;
		case SELF:
			break;
		}
	}

	public int getLinkKarma() {
		return this.linkKarma;
	}

	public int getCommentKarma() {
		return this.commentKarma;
	}
}