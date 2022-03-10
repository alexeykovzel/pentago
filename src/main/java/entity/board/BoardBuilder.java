package entity.board;

import static entity.board.Board.DIM;

/**
 * Classes that build a textual representation of the board.
 *
 * @author Aliaksei Kouzel
 */
public class BoardBuilder {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    /**
     * Get a string representation of the board as well as the numbering of the fields
     * and the rotation hint overview.
     *
     * @return string representation of the board
     */
    public String build(Marble[] fields) {
        String[] hintFields = new String[DIM * DIM];
        String[] marbles = new String[DIM * DIM];
        for (int i = 0; i < (DIM * DIM); i++) {
            marbles[i] = fields[i].display;
            hintFields[i] = String.valueOf(i);
        }

        String delimiter = "       ";
        String[] boardView = buildSkeleton(marbles);
        String[] boardHint = buildSkeleton(hintFields);
        String[] rotationHint = buildRotationHint();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < DIM * 2 - 1; i++) {
            result.append(boardView[i]).append(delimiter);
            result.append(boardHint[i]).append(delimiter);
            if (rotationHint.length > i) {
                result.append(rotationHint[i]);
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Build a string skeleton of the given fields.
     *
     * @param fields string field values
     * @return string overview of the fields
     */
    private String[] buildSkeleton(String[] fields) {
        String[] skeleton = new String[DIM * 2 - 1];
        for (int i = 0; i < DIM; i++) {
            StringBuilder fieldsLine = new StringBuilder().append("|");
            StringBuilder separator = new StringBuilder().append("|");

            for (int j = 0; j < DIM; j++) {
                boolean isCenter = j == (DIM / 2 - 1);
                boolean isSubBorder = i == (DIM / 2 - 1);

                String field = fields[i * DIM + j];
                field = (field.length() > 3)
                        ? ("  " + field.substring(0, 2) + " ")
                        : (String.format("%3s", field) + "  ");

                fieldsLine.append(field).append(isCenter ? (ANSI_GREEN + "||" + ANSI_RESET) : "|");
                separator.append(isSubBorder ? (ANSI_GREEN + "=====" + ANSI_RESET) : "-----");
                separator.append(isCenter ? (ANSI_GREEN + "||" + ANSI_RESET) : "|");
            }

            skeleton[i * 2] = fieldsLine.toString();
            boolean isBorder = i == DIM - 1;
            if (!isBorder) skeleton[i * 2 + 1] = separator.toString();
        }
        return skeleton;
    }

    /**
     * Build the rotation hint overview.
     *
     * @return rotation hint overview
     * @pure
     * @ensures \result != null
     */
    private String[] buildRotationHint() {
        return new String[]{
                "Rotation indexes:",
                "0 -> top left counter-clockwise",
                "1 -> top left clockwise",
                "2 -> top right counter-clockwise",
                "3 -> top right clockwise",
                "4 -> bottom left counter-clockwise",
                "5 -> bottom left clockwise",
                "6 -> bottom right counter-clockwise",
                "7 -> bottom right clockwise"
        };
    }
}
