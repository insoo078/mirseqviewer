package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;

public class MsbRCTColumnModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String						type;
	private String						id;
	private List<MsbRCTColumnModel>		child;
	private String						group;
	
	public MsbRCTColumnModel() {
		this.type = null;
		this.id = null;
		this.child = null;
		this.group = null;
	}

	public MsbRCTColumnModel( String type, String id, List<MsbRCTColumnModel> child ) {
		this.type	= type;
		this.id		= id;
		this.child	= child;
		this.group	= null;
	}
	
	public MsbRCTColumnModel( String type, Group group ) {
		this( type, group.getGroupId(), null );

		if( group.getSample() != null ) {
			this.child = new ArrayList<MsbRCTColumnModel>();
			List<Sample> smpList = group.getSample();
			for(int i=0; i<smpList.size(); i++) {
				MsbRCTColumnModel column = new MsbRCTColumnModel( JMsbSysConst.SAMPLE_HEADER_PREFIX, smpList.get(i).getName(), null );
				this.child.add( column );
			}
		}
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public void setColumnType( String type ) {
		this.type = type;
	}
	
	public String getColumnType() {
		return this.type;
	}

	public String getColumnId() {
		return this.id;
	}
	
	public List<MsbRCTColumnModel> getChildColumnList() {
		return this.child;
	}
	
	public boolean isGroup() {
		if( this.child == null )			return false;
		else if( this.child.size() == 0 )	return false;

		return true;
	}
	
	public boolean hasSubNode(String name) {
		for(int i=0; i<this.child.size(); i++) {
			if( name.equals( this.child.get(i).getColumnId() ) ) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getChildColumnIdList() {
		if( this.isGroup() ) {
			List<String> list = new ArrayList<String>();
			Iterator<MsbRCTColumnModel> iter = this.child.iterator();
			while( iter.hasNext() ) {
				list.add( iter.next().getColumnId() );
			}
			return list;
		}
		
		return null;
	}
	
	public void addChildColumn(MsbRCTColumnModel column) {
		if( this.isGroup() )	{
			if( this.child == null )	this.child  = new ArrayList<MsbRCTColumnModel>();
			this.child.add( column );
		}
		else					{
			this.child = new ArrayList<MsbRCTColumnModel>();
			this.child.add( column );
		}
	}
	
	public void removeChildColumn(int pos) {
		if( this.isGroup() && this.child != null )	this.child.remove( pos );
	}
	
	public MsbRCTColumnModel clone() {
		MsbRCTColumnModel model = new MsbRCTColumnModel( this.type, this.id, null );
		model.setGroup( this.group );
		return model;
	}
}