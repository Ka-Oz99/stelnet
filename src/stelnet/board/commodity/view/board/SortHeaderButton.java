package stelnet.board.commodity.view.board;

import com.fs.starfarer.api.ui.IntelUIAPI;
import stelnet.board.commodity.CommodityBoard;
import stelnet.util.StelnetHelper;
import uilib.Button;
import uilib.UiConstants;
import uilib.property.Size;

/**
 * A clickable column title. Clicking cycles that column through ascending, descending and unsorted. The "#" column
 * button resets to the original order.
 */
public class SortHeaderButton extends Button {

    private final int column;

    public SortHeaderButton(String title, float width, int column, boolean isActive, boolean isDescending) {
        super(new Size(width, UiConstants.TABLE_ROW_HEIGHT), buildTitle(title, isActive, isDescending), true);
        this.column = column;
        setPadding(0);
        setHighlight(isActive);
    }

    @Override
    public void onConfirm(IntelUIAPI ui) {
        CommodityBoard board = StelnetHelper.getInstance(CommodityBoard.class);
        board.getRenderableState().toggleSort(column);
    }

    private static String buildTitle(String title, boolean isActive, boolean isDescending) {
        if (!isActive) {
            return title;
        }
        return title + (isDescending ? " (-)" : " (+)");
    }
}
