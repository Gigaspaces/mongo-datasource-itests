package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

import java.util.Date;


public class IssueDocument extends SpaceDocument implements Issue {

    private static final String ISSUE = "Issue";
    private static final String key = "key";
    private static final String reporter = "reporter";
    private static final String created = "created";
    private static final String updated = "updated";
    private static final String votes = "votes";
    private static final String priority = "priority";
    private static final String votesRep = "votesRep";


    public static SpaceTypeDescriptor getTypeDescriptor()
    {
        return new SpaceTypeDescriptorBuilder(ISSUE)
                .supportsDynamicProperties(true)
                .addFixedProperty(key  , Integer.class)
                .idProperty(key)
                .documentWrapperClass(IssueDocument.class)
                .create();
    }

    public IssueDocument()
    {
        super(ISSUE);
    }

    public IssueDocument(Integer key)
    {
        this(key, getCurrentSystemUser());
    }

    public IssueDocument(Integer key, String reporter)
    {
        this();
        setKey(key);
        setReporter(new User(reporter));
        setPriority(Priority.TRIVIAL);
        setVotes(0);

        //defaults
        long now = System.currentTimeMillis();
        setCreated(new Date(now));
        setUpdated(new Date(now));
        setVotesRep(String.valueOf(votes));
    }

    public Date getCreated() {
        return getProperty(created);
    }

    public void setCreated(Date _created) {
        setProperty(created, _created);
    }

    public Date getUpdated() {
        return getProperty(updated);
    }

    public void setUpdated(Date _updated) {
        setProperty(updated, _updated);
    }

    public String getVotesRep() {
        return getProperty(votesRep);
    }

    public void setVotesRep(String _votesRep) {
        setProperty(votesRep, _votesRep);
    }

    public Integer getVotes() {
        return getProperty(votes);
    }

    public void setVotes(Integer _votes) {
        setProperty(votes, _votes);
    }

    public void setKey(Integer _key) {
        setProperty(key, _key);
    }

    public Integer getKey() {
        return getProperty(key);
    }

    public User getReporter() {
        return getProperty(reporter);
    }

    public void setReporter(User _reporter) {
        setProperty(reporter, _reporter);
    }

    public Priority getPriority() {
        return getProperty(priority);
    }

    public void setPriority(Priority _priority) {
        setProperty(priority, _priority);
    }

    private static String getCurrentSystemUser()
    {
        return System.getProperty("user.name", "anonymous");
    }

    public Priority vote(){
        setUpdated(new Date( System.currentTimeMillis()));
        setVotes(getVotes()+1);
        setVotesRep(String.valueOf(getVotes()));
        Priority votedPriority = Priority.TRIVIAL;
        Priority previous = getPriority();

        switch (getVotes())
        {
            case 5:
                votedPriority = Priority.MINOR;
                break;
            case 10:
                votedPriority = Priority.MEDIUM;
                break;
            case 15:
                votedPriority = Priority.MAJOR;
                break;
            case 20:
                votedPriority = Priority.CRITICAL;
                break;
            case 25:
                votedPriority = Priority.BLOCKER;
                break;
        }

        if (votedPriority.ordinal() > getPriority().ordinal())
            setPriority(votedPriority);

        return previous;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\t KEY-" + getKey() );
        sb.append("\t reporter: " + (getReporter() == null ? "Unassigned" : getReporter() ));
        sb.append("\t votes: " + getVotes());
        sb.append("\t priority: " + getPriority());
        sb.append("\t created: " + getCreated());
        sb.append("\t updated: " + getUpdated());
        return sb.toString();
    }

    /* @see java.lang.Object#hashCode() */
    @Override
    public int hashCode()
    {
        return getKey().hashCode();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
	/* (non-Javadoc)
	 * @see com.gigaspaces.common_data.issue.Issue#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object obj)
    {
        if ( !(obj instanceof IssueDocument) )
            return false;

        IssueDocument otherIssue = (IssueDocument)obj;

        //verify key
        if (this.getKey() == null && otherIssue.getKey() != null)
            return false;
        else	if (this.getKey() != null && !this.getKey().equals(otherIssue.getKey()) )
            return false;

        //verify reporter
        if (this.getReporter() == null && otherIssue.getReporter() != null)
            return false;
        else if (this.getReporter() != null && !this.getReporter().equals(otherIssue.getReporter()) )
            return false;

        //verify votes
        if (this.getVotes() == null && otherIssue.getVotes() != null)
            return false;
        else if (this.getVotes() != null && !this.getVotes().equals(otherIssue.getVotes()) )
            return false;

        //verify priority
        if (this.getPriority() == null && otherIssue.getPriority() != null)
            return false;
        else if (this.getPriority() != null	&& !this.getPriority().equals(otherIssue.getPriority()) )
            return false;

        //verify created
        if (this.getCreated() == null && otherIssue.getCreated() != null)
            return false;
        else if (this.getCreated() != null && !this.getCreated().equals(otherIssue.getCreated()) )
            return false;

        //verify updated
        if (this.getUpdated() == null && otherIssue.getUpdated() != null)
            return false;
        else if (this.getUpdated() != null && !this.getUpdated().equals(otherIssue.getUpdated()) )
            return false;

        //verify votesRep
        if (this.getVotesRep() == null && otherIssue.getVotesRep() != null)
            return false;
        else if (this.getVotesRep() != null && !this.getVotesRep().equals(otherIssue.getVotesRep()) )
            return false;

        //all properties equal
        return true;
    }

    /**
     * Compares to Issue by their key ordering.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Issue otherIssue)
    {
        return this.getKey().compareTo(otherIssue.getKey());
    }

    /**
     * @see java.lang.Object#clone()
     * @see #shallowClone()
     * @see #deepClone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /* (non-Javadoc)
     * @see com.gigaspaces.common_data.issue.Issue#shallowClone()
     */
    public Issue shallowClone()
    {
        try{
            Issue shallow = (Issue)clone();
            return shallow;

        }catch (CloneNotSupportedException e){
            //can't happen
        }

        return null;
    }

    /* (non-Javadoc)
     * @see com.gigaspaces.common_data.issue.Issue#deepClone()
     */
    public Issue deepClone()
    {
        try{
            IssueDocument deep = (IssueDocument)clone();
            deep.setKey(getKey());
            deep.setReporter(new User(getReporter().getUsername()));
            deep.setVotes(getVotes());
            deep.setVotesRep(String.valueOf(deep.getVotes()));

            return deep;

        }catch (CloneNotSupportedException e){
            //can't happen
        }

        return null;
    }
}
