package co.uk.rushorm.rushserver.example.modal;

import java.util.Date;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by Stuart on 03/03/15.
 */
public class TestClass extends RushObject {

    private String stringField;
    private double doubleField;
    private int intField;
    private long longField;
    private short shortField;
    private boolean booleanField;
    private Date dateField;
    
    public TestClass2 testClass2;
    
    @RushList(classname = "co.uk.rushorm.rushserver.example.modal.TestClass2")
    public List<TestClass2> testClass2List;
    
}
