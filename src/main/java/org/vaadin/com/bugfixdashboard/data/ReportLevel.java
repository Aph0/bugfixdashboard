package org.vaadin.com.bugfixdashboard.data;

import java.util.ArrayList;
import java.util.List;

public class ReportLevel {

    private final ReportLevel parent;

    private final String name;

    private final Integer value;

    private List<ReportLevel> children = new ArrayList<ReportLevel>();

    public ReportLevel(ReportLevel parent, String name, Integer value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public void addChild(ReportLevel reportLevel) {
        children.add(reportLevel);
    }

    public List<ReportLevel> getChildren() {
        return children;
    }

    /**
     * If parent is null, it is considered root
     * 
     * @return
     */
    public ReportLevel getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Returns the depth. Root is considered level 1
     * 
     * @return
     */
    public int getLevel() {
        if (parent == null) {
            return 1;
        } else {
            return parent.getLevel() + 1;
        }
    }

    /**
     * Two report levels are equal if their name and level are same, even if the
     * values differ
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }
        ReportLevel other = (ReportLevel) obj;
        if (name.equals(other.name) && getLevel() == other.getLevel()) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = code * 17 + (name == null ? 0 : name.hashCode());
        code = code * 31 + getLevel();
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

}
