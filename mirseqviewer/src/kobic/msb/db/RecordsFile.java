package kobic.msb.db;

import java.io.*;
import java.util.*;

@SuppressWarnings("rawtypes")
public class RecordsFile extends BaseRecordsFile {

  /**
   * Hashtable which holds the in-memory index. For efficiency, the entire index 
   * is cached in memory. The hashtable maps a key of type String to a RecordHeader.
   */
	protected Hashtable memIndex;    

  /**
   * Creates a new database file.  The initialSize parameter determines the 
   * amount of space which is allocated for the index.  The index can grow 
   * dynamically, but the parameter is provide to increase 
   * efficiency. 
   */
	public RecordsFile(String dbPath, int initialSize) throws IOException, RecordsFileException {
	  
		super(dbPath, initialSize);
		memIndex = new Hashtable(initialSize);
	}

  /**
   * Opens an existing database and initializes the in-memory index. 
   */
	@SuppressWarnings("unchecked")
	public RecordsFile(String dbPath, String accessFlags) throws IOException, RecordsFileException {
		super(dbPath, accessFlags);
		int numRecords = readNumRecordsHeader();
		memIndex = new Hashtable(numRecords);
		for (int i = 0; i < numRecords; i++) {
			String key = readKeyFromIndex(i);
			RecordHeader header = readRecordHeaderFromIndex(i);
			header.setIndexPosition(i);
			memIndex.put(key, header);
		}
	}

  
  /**
   * Returns an enumeration of all the keys in the database.
   */
	@Override
	public synchronized Enumeration enumerateKeys() {
		return memIndex.keys();
	}

  /**
   * Returns the current number of records in the database. 
   */
	@Override
	public synchronized int getNumRecords() {
		return memIndex.size();
	}

  /**
   * Checks if there is a record belonging to the given key. 
   */
	@Override
	public synchronized boolean recordExists(String key) {
		return memIndex.containsKey(key);
	}

  /**
   * Maps a key to a record header by looking it up in the in-memory index.
   */
	@Override
	protected RecordHeader keyToRecordHeader(String key) throws RecordsFileException {
		RecordHeader h = (RecordHeader)memIndex.get(key);
		if (h==null) {
			throw new RecordsFileException("Key not found: " + key);
		} 
		return h;
	}

  /**
   * This method searches the file for free space and then returns a RecordHeader 
   * which uses the space. (O(n) memory accesses)
   */
	@Override
	protected RecordHeader allocateRecord(String key, int dataLength) throws RecordsFileException, IOException {
    // search for empty space
		RecordHeader newRecord = null;
		Enumeration e = memIndex.elements();
		while (e.hasMoreElements()) {
			RecordHeader next = (RecordHeader)e.nextElement();
			@SuppressWarnings("unused")
			int free = next.getFreeSpace();
			if (dataLength <= next.getFreeSpace()) {
				newRecord = next.split();
				writeRecordHeaderToIndex(next);
				break;
			}
		}
		if (newRecord == null) {
	      // append record to end of file - grows file to allocate space
	      long fp = getFileLength();
	      setFileLength(fp + dataLength);
	      newRecord = new RecordHeader(fp, dataLength);
	    } 
	    return newRecord;
	}

  /**
   * Returns the record to which the target file pointer belongs - meaning the specified location
   * in the file is part of the record data of the RecordHeader which is returned.  Returns null if 
   * the location is not part of a record. (O(n) mem accesses)
   */
	@Override
	protected RecordHeader getRecordAt(long targetFp) throws RecordsFileException {
	    Enumeration e = memIndex.elements();
	    while (e.hasMoreElements()) {
	      RecordHeader next = (RecordHeader) e.nextElement();
	      if (targetFp >= next.dataPointer &&
		  targetFp < next.dataPointer + next.dataCapacity) {
		return next;
	      }
	    }
	    return null;
	}


  /**
   * Closes the database. 
   */
	@Override
	public synchronized void close() throws IOException, RecordsFileException {
	    try {
	      super.close();
	    } finally {
	      memIndex.clear();
	      memIndex = null;
	    }
	}

  /**
   * Adds the new record to the in-memory index and calls the super class add
   * the index entry to the file. 
   */
	@SuppressWarnings("unchecked")
	@Override
	protected void addEntryToIndex(String key, RecordHeader newRecord, int currentNumRecords) throws IOException, RecordsFileException {
	    super.addEntryToIndex(key, newRecord, currentNumRecords);
	    memIndex.put(key, newRecord);   
	}
 
  /**
   * Removes the record from the index. Replaces the target with the entry at the 
   * end of the index. 
   */
	@Override
	protected void deleteEntryFromIndex(String key, RecordHeader header, int currentNumRecords) throws IOException, RecordsFileException {
	    super.deleteEntryFromIndex(key, header, currentNumRecords);
	    @SuppressWarnings("unused")
		RecordHeader deleted = (RecordHeader)memIndex.remove(key);
	}
}






