package bjtu.makeupapp.util;

import android.os.Build;
import android.renderscript.*;
import android.support.annotation.RequiresApi;

/**
 * Created by lenovo on 2017/9/7.
 */

public class ScriptC_Rotate extends ScriptC {
    private static final String __rs_resource_name = "rotate";
    // Constructor
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  ScriptC_Rotate(RenderScript rs) {
        super(rs,
                __rs_resource_name,
                RotateBitCode.getBitCode32(),
                RotateBitCode.getBitCode64());
        __ALLOCATION = Element.ALLOCATION(rs);
        __I32 = Element.I32(rs);
        __U8_4 = Element.U8_4(rs);
    }

    private Element __ALLOCATION;
    private Element __I32;
    private Element __U8_4;
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_I32;
    private final static int mExportVarIdx_inImage = 0;
    private Allocation mExportVar_inImage;
    public synchronized void set_inImage(Allocation v) {
        setVar(mExportVarIdx_inImage, v);
        mExportVar_inImage = v;
    }

    public Allocation get_inImage() {
        return mExportVar_inImage;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Script.FieldID getFieldID_inImage() {
        return createFieldID(mExportVarIdx_inImage, null);
    }

    private final static int mExportVarIdx_inWidth = 1;
    private int mExportVar_inWidth;
    public synchronized void set_inWidth(int v) {
        setVar(mExportVarIdx_inWidth, v);
        mExportVar_inWidth = v;
    }

    public int get_inWidth() {
        return mExportVar_inWidth;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Script.FieldID getFieldID_inWidth() {
        return createFieldID(mExportVarIdx_inWidth, null);
    }

    private final static int mExportVarIdx_inHeight = 2;
    private int mExportVar_inHeight;
    public synchronized void set_inHeight(int v) {
        setVar(mExportVarIdx_inHeight, v);
        mExportVar_inHeight = v;
    }

    public int get_inHeight() {
        return mExportVar_inHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Script.FieldID getFieldID_inHeight() {
        return createFieldID(mExportVarIdx_inHeight, null);
    }

    //private final static int mExportForEachIdx_root = 0;
    private final static int mExportForEachIdx_rotate_90_clockwise = 1;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Script.KernelID getKernelID_rotate_90_clockwise() {
        return createKernelID(mExportForEachIdx_rotate_90_clockwise, 59, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void forEach_rotate_90_clockwise(Allocation ain, Allocation aout) {
        forEach_rotate_90_clockwise(ain, aout, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void forEach_rotate_90_clockwise(Allocation ain, Allocation aout, Script.LaunchOptions sc) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        Type t0, t1;        // Verify dimensions
        t0 = ain.getType();
        t1 = aout.getType();
        if ((t0.getCount() != t1.getCount()) ||
                (t0.getX() != t1.getX()) ||
                (t0.getY() != t1.getY()) ||
                (t0.getZ() != t1.getZ()) ||
                (t0.hasFaces()   != t1.hasFaces()) ||
                (t0.hasMipmaps() != t1.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between parameters ain and aout!");
        }

        forEach(mExportForEachIdx_rotate_90_clockwise, ain, aout, null, sc);
    }

    private final static int mExportForEachIdx_rotate_270_clockwise = 2;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Script.KernelID getKernelID_rotate_270_clockwise() {
        return createKernelID(mExportForEachIdx_rotate_270_clockwise, 59, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void forEach_rotate_270_clockwise(Allocation ain, Allocation aout) {
        forEach_rotate_270_clockwise(ain, aout, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void forEach_rotate_270_clockwise(Allocation ain, Allocation aout, Script.LaunchOptions sc) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        Type t0, t1;        // Verify dimensions
        t0 = ain.getType();
        t1 = aout.getType();
        if ((t0.getCount() != t1.getCount()) ||
                (t0.getX() != t1.getX()) ||
                (t0.getY() != t1.getY()) ||
                (t0.getZ() != t1.getZ()) ||
                (t0.hasFaces()   != t1.hasFaces()) ||
                (t0.hasMipmaps() != t1.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between parameters ain and aout!");
        }

        forEach(mExportForEachIdx_rotate_270_clockwise, ain, aout, null, sc);
    }

}
