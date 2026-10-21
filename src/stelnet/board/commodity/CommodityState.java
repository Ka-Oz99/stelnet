package stelnet.board.commodity;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import stelnet.util.StelnetHelper;
import uilib.Renderable;
import uilib.RenderableState;
import uilib.property.Size;

@Getter
@Setter
@Log4j
public class CommodityState implements RenderableState {

    private String commodityId = Commodities.SUPPLIES;
    private CommodityAction activeTab = CommodityAction.BUY;

    /** Column index of the active sort (0 = none, keeps original order). Defaults are old-save friendly. */
    private int sortColumn = 0;

    private boolean sortDescending = false;
    private transient IntelTracker intelTracker = new IntelTracker();

    public Object readResolve() {
        intelTracker = new IntelTracker();
        correctSelectedCommodity();
        return this;
    }

    @Override
    public List<Renderable> toRenderableList(Size size) {
        return new CommodityView(commodityId, activeTab, intelTracker, sortColumn, sortDescending).create(size);
    }

    /** Cycles a column through ascending, descending and unsorted. Column 0 resets sorting. */
    public void toggleSort(int column) {
        if (column <= 0) {
            resetSort();
        } else if (sortColumn != column) {
            sortColumn = column;
            sortDescending = false;
        } else if (!sortDescending) {
            sortDescending = true;
        } else {
            resetSort();
        }
    }

    public void resetSort() {
        sortColumn = 0;
        sortDescending = false;
    }

    /**
     * The currently selected commodity id could no longer be valid if player removed a mod mid-save.
     * Make sure what we display does exist, or default to supplies.
     */
    private void correctSelectedCommodity() {
        if (!StelnetHelper.hasCommodity(commodityId)) {
            log.warn("Commodity with id " + commodityId + " no longer exists, resetting to supplies");
            commodityId = Commodities.SUPPLIES;
        }
    }
}
