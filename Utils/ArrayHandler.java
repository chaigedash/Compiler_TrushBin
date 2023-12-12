package Utils;

import LLVM.Value;

import java.util.ArrayList;

public class ArrayHandler {
    private static final ArrayHandler INSTANCE = new ArrayHandler();
    public static ArrayHandler getInstance() {
        return INSTANCE;
    }
    public String getType (Value.Type priType, Integer dimension1, Integer dimension2) {
        String type = "";
        if (dimension1 != null && dimension1 != 0) {
            type += "[" + String.valueOf(dimension1) + " x ";
            if (dimension2 != null && dimension2 != 0) {
                type += "[" + String.valueOf(dimension2) + " x ";
            }
            type += priType.toString();
            if (dimension2 != null && dimension2 != 0) {
                type += "]";
            }
            type += "]";
        }
        else { // 0 dimension
            type = priType.toString();
        }
        return type;
    }
    public String getInit (Value.Type priType, ArrayList<Integer> value, Integer dimension1, Integer dimension2) {
        String init = "";
        if (dimension1 != null && dimension1 != 0) {
            if (value != null) {
                init += "[";
                if (dimension2 != null && dimension2 != 0) {
                    for (int i = 0; i < value.size(); i += dimension2) {
                        if (i > 0) {
                            init += ", ";
                        }
                        init += "[" + dimension2 + " x " + priType.toString() + "] [";
                        for (int j = i; j < i + dimension2; j++) {
                            if (j > i) {
                                init += ", ";
                            }
                            init += priType.toString() + " " + value.get(j);
                        }
                        init += "]";
                    }
                }
                else {
                    for (int i = 0; i < value.size(); i++) {
                        if (i > 0) {
                            init += ", ";
                        }
                        init +=  priType.toString() + " " + value.get(i);
                    }
                }
                if (dimension2 != null && dimension2 != 0) {
                }
                init += "]";
            }
            else {
                // init value is null
                init = "zeroinitializer";
            }
        }
        else { // 0 dimension
            if (value == null) {
                init = "0";
            }
            else {
                init = String.valueOf(value.get(0));
            }
        }
        return init;
    }
}
