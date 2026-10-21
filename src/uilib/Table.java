package uilib;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uilib.property.Size;

@RequiredArgsConstructor
@Setter
public class Table extends RenderableComponent {

    private final String title;
    private final float width;
    private final float maxHeight;
    private final TableContent tableContent;

    private FactionAPI faction = Global.getSector().getPlayerFaction();
    private String noRowsDescription = "No rows present.";
    private List<? extends Button> sortButtons = new ArrayList<Button>();

    @Override
    public Size getSize() {
        float tableHeight = UiConstants.TABLE_ROW_HEIGHT * (tableContent.getRows().size() + 1) + 8;
        float height = maxHeight == 0 ? tableHeight : Math.min(maxHeight, tableHeight);
        return new Size(width, height);
    }

    @Override
    public void render(CustomPanelAPI panel, float x, float y) {
        super.render(panel, x - 4, y);
    }

    @Override
    public void render(TooltipMakerAPI tooltip) {
        List<? extends TableContentRow> rows = tableContent.getRows();
        Object[] headers = tableContent.getHeaders(width - 24);
        boolean hasSortButtons = !sortButtons.isEmpty() && !rows.isEmpty();
        if (hasSortButtons) {
            // the clickable buttons are drawn over the header row, so blank the built-in titles
            for (int i = 0; i + 1 < headers.length; i += 2) {
                headers[i] = " ";
            }
        }
        tooltip.beginTable(faction, UiConstants.TABLE_ROW_HEIGHT, headers);
        for (TableContentRow row : rows) {
            tooltip.addRow(row.buildObjectArray());
        }
        if (rows.isEmpty()) {
            tooltip.addPara(noRowsDescription, 0);
            return;
        }
        tooltip.addTable(title, 0, 1);
        if (hasSortButtons) {
            renderSortButtons(tooltip);
        }
    }

    /** Pixels to shift the clickable titles right (+) or left (-) so they sit over the table columns. */
    private static final float SORT_BUTTONS_X_OFFSET = 8;

    /** Draws the clickable titles on top of the table's header row, left to right from the table's left edge. */
    private void renderSortButtons(TooltipMakerAPI tooltip) {
        float x = SORT_BUTTONS_X_OFFSET;
        for (Button button : sortButtons) {
            button.render(tooltip);
            tooltip.getPrev().getPosition().inTL(x, 0);
            x += button.getSize().getWidth();
        }
    }
}
