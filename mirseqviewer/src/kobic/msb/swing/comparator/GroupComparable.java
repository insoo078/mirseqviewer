package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.server.model.jaxb.Msb;

public class GroupComparable implements Comparator<Msb.Project.Samples.Group> {
    @Override
    public int compare(Msb.Project.Samples.Group o1, Msb.Project.Samples.Group o2) {
    	return o1.getGroupId().compareTo( o2.getGroupId() );
    }
}
