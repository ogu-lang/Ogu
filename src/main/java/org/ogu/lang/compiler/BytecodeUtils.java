package org.ogu.lang.compiler;

import org.objectweb.asm.Opcodes;
import org.ogu.lang.codegen.bytecode_generation.MathOperationBS;
import org.ogu.lang.codegen.jvm.JvmTypeCategory;
import org.ogu.lang.parser.ast.expressions.MathOpExpressionNode;

/**
 * Created by ediaz on 11/2/16.
 */
public class BytecodeUtils {

    private BytecodeUtils() {
        // prevent instantiation
    }

    /*
    public static RelationalOperationBS createRelationOperation(RelationalOperation.Operator operator) {
        int jumpOpcode;
        switch (operator) {
            case EQUAL:
                jumpOpcode = Opcodes.IF_ICMPNE;
                break;
            case DIFFERENT:
                jumpOpcode = Opcodes.IF_ICMPEQ;
                break;
            case LESS:
                jumpOpcode = Opcodes.IF_ICMPGE;
                break;
            case LESSEQ:
                jumpOpcode = Opcodes.IF_ICMPGT;
                break;
            case MORE:
                jumpOpcode = Opcodes.IF_ICMPLE;
                break;
            case MOREEQ:
                jumpOpcode = Opcodes.IF_ICMPLT;
                break;
            default:
                throw new UnsupportedOperationException(operator.name());
        }
        return new RelationalOperationBS(jumpOpcode);
    }
    */

    public static MathOperationBS createMathOperation(JvmTypeCategory operandsType, MathOpExpressionNode.Operator operator) {
        int opcode;
        switch (operator) {
            case MULTIPLICATION:
                switch (operandsType) {
                    case INT:
                        opcode = Opcodes.IMUL;
                        break;
                    case LONG:
                        opcode = Opcodes.LMUL;
                        break;
                    case FLOAT:
                        opcode = Opcodes.FMUL;
                        break;
                    case DOUBLE:
                        opcode = Opcodes.DMUL;
                        break;
                    default:
                        throw new UnsupportedOperationException(operator + " " +operandsType.name());
                }
                break;
            case SUM:
                switch (operandsType) {
                    case INT:
                        opcode = Opcodes.IADD;
                        break;
                    case LONG:
                        opcode = Opcodes.LADD;
                        break;
                    case FLOAT:
                        opcode = Opcodes.FADD;
                        break;
                    case DOUBLE:
                        opcode = Opcodes.DADD;
                        break;
                    default:
                        throw new UnsupportedOperationException(operator + " " +operandsType.name());
                }
                break;
            case SUBTRACTION:
                switch (operandsType) {
                    case INT:
                        opcode = Opcodes.ISUB;
                        break;
                    case LONG:
                        opcode = Opcodes.LSUB;
                        break;
                    case FLOAT:
                        opcode = Opcodes.FSUB;
                        break;
                    case DOUBLE:
                        opcode = Opcodes.DSUB;
                        break;
                    default:
                        throw new UnsupportedOperationException(operator + " " +operandsType.name());
                }
                break;
            case DIVISION:
                switch (operandsType) {
                    case INT:
                        opcode = Opcodes.IDIV;
                        break;
                    case LONG:
                        opcode = Opcodes.LDIV;
                        break;
                    case FLOAT:
                        opcode = Opcodes.FDIV;
                        break;
                    case DOUBLE:
                        opcode = Opcodes.DDIV;
                        break;
                    default:
                        throw new UnsupportedOperationException(operator + " " +operandsType.name());
                }
                break;
            default:
                throw new UnsupportedOperationException(operator.name());
        }
        return new MathOperationBS(opcode);
    }
}
