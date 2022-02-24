package ir.ac.ut.ece.rv.utility;

import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.Pair;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class RebecaCompilerUtility {

    public static RebecaModel getRebecaModelOf(String filePath) {
        Set<CompilerFeature> features = new HashSet<>();
        features.add(CompilerFeature.CORE_2_2);

        File rebecaFile = new File(filePath);

        RebecaCompiler compiler = new RebecaCompiler();
        Pair<RebecaModel, SymbolTable> result = compiler.compileRebecaFile(rebecaFile, features);
        return result.getFirst();
    }
}
