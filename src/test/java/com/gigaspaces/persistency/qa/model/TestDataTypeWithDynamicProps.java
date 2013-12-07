package com.gigaspaces.persistency.qa.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.gigaspaces.document.SpaceDocument;


public interface TestDataTypeWithDynamicProps {
    String getId();
    void setId(String id);
    
    SerializableIssuePojo getFixedIssue();
    void setFixedIssue(SerializableIssuePojo fixedIssue);
    
    SerializableIssuePojo getDynamicIssue();
    void setDynamicIssue(SerializableIssuePojo dynamicIssue);
    
    Long getFixedLong();
    void setFixedLong(Long fixedLong);
    
    Long getDynamicLong();
    void setDynamicLong(Long dynamicLong);
    
    SpaceDocument getFixedDocument();
    void setFixedDocument(SpaceDocument fixedDocument);
    
    SpaceDocument getDynamicDocument();
    void setDynamicDocument(SpaceDocument dynamicDocument);
    
    SerializableIssuePojo[] getFixedIssueArray();
    void setFixedIssueArray(SerializableIssuePojo[] fixedIssueArray);
    
    SerializableIssuePojo[] getDynamicIssueArray();
    void setDynamicIssueArray(SerializableIssuePojo[] dynamicIssueArray);
    
    Long[] getFixedLongArray();
    void setFixedLongArray(Long[] fixedLongArray);
    
    Long[] getDynamicLongArray();
    void setDynamicLongArray(Long[] dynamicLongArray);
    
    SpaceDocument[] getFixedDocumentArray();
    void setFixedDocumentArray(SpaceDocument[] fixedDocumentArray);
    
    SpaceDocument[] getDynamicDocumentArray();
    void setDynamicDocumentArray(SpaceDocument[] dynamicDocumentArray);
    
    ArrayList<SerializableIssuePojo> getFixedIssueList();
    void setFixedIssueList(ArrayList<SerializableIssuePojo> fixedIssueList);
    
    ArrayList<SerializableIssuePojo> getDynamicIssueList();
    void setDynamicIssueList(ArrayList<SerializableIssuePojo> dynamicIssueList);
    
    ArrayList<Long> getFixedLongList();
    void setFixedLongList(ArrayList<Long> fixedLongList);
    
    ArrayList<Long> getDynamicLongList();
    void setDynamicLongList(ArrayList<Long> dynamicLongList);
    
    ArrayList<SpaceDocument> getFixedDocumentList();
    void setFixedDocumentList(ArrayList<SpaceDocument> fixedDocumentList);
    
    ArrayList<SpaceDocument> getDynamicDocumentList();
    void setDynamicDocumentList(ArrayList<SpaceDocument> dynamicDocumentList);
    
    Set<SerializableIssuePojo> getFixedIssueSet();
    void setFixedIssueSet(Set<SerializableIssuePojo> fixedIssueSet);

    Set<SerializableIssuePojo> getDynamicIssueSet();
    void setDynamicIssueSet(Set<SerializableIssuePojo> dynamicIssueSet);

    Set<Long> getFixedLongSet();
    void setFixedLongSet(Set<Long> fixedLongSet);
    
    Set<Long> getDynamicLongSet();
    void setDynamicLongSet(Set<Long> dynamicLongSet);
    
    Set<SpaceDocument> getFixedDocumentSet();
    void setFixedDocumentSet(Set<SpaceDocument> fixedDocumentSet);
    
    Set<SpaceDocument> getDynamicDocumentSet();
    void setDynamicDocumentSet(Set<SpaceDocument> dynamicDocumentSet);
    
    Map<Long, SerializableIssuePojo> getFixedMap();
    void setFixedMap(Map<Long, SerializableIssuePojo> fixedMap);
    
    Map<Long, SerializableIssuePojo> getDynamicMap();
    void setDynamicMap(Map<Long, SerializableIssuePojo> dynamicMap);
    
    Map<Long, SpaceDocument> getFixedDocumentMap();
    void setFixedDocumentMap(Map<Long, SpaceDocument> fixedDocumentMap);
    
    Map<Long, SpaceDocument> getDynamicDocumentMap();
    void setDynamicDocumentMap(Map<Long, SpaceDocument> dynamicDocumentMap);
}
