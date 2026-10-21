package stelnet.board.commodity.view.board;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import stelnet.board.commodity.CommodityAction;
import stelnet.board.commodity.IntelTracker;
import stelnet.board.commodity.table.BuyTableContent;
import stelnet.board.commodity.table.MarketTableContent;
import stelnet.board.commodity.table.MarketTableRow;
import stelnet.board.commodity.table.SellTableContent;
import uilib.HorizontalViewContainer;
import uilib.Renderable;
import uilib.RenderableFactory;
import uilib.property.Size;

@AllArgsConstructor
public class IntelViewFactory implements RenderableFactory {

    private final String commodityId;
    private final CommodityAction commodityAction;
    private final IntelTracker tracker;
    private final int sortColumn;
    private final boolean sortDescending;

    @Override
    public List<Renderable> create(Size size) {
        List<MarketAPI> markets = getMarketsInTableOrder();
        int numberOfButtons = calcNumberOfButtons(markets, size);

        List<Renderable> buttons = new LinkedList<>();
        for (int i = 0; i < numberOfButtons; i++) {
            MarketAPI market = markets.get(i);
            buttons.add(new IntelViewButton(i + 1, commodityId, market, tracker));
        }

        HorizontalViewContainer rows = new HorizontalViewContainer(buttons);
        rows.setSize(size);
        return Collections.<Renderable>singletonList(rows);
    }

    /**
     * Button N must belong to row N of the table, so use the same order (including the active sort) as the table.
     */
    private List<MarketAPI> getMarketsInTableOrder() {
        List<MarketAPI> markets = commodityAction.getMarkets(commodityId);
        MarketTableContent content;
        if (commodityAction == CommodityAction.BUY) {
            content = new BuyTableContent(commodityId, markets);
        } else if (commodityAction == CommodityAction.SELL) {
            content = new SellTableContent(commodityId, markets);
        } else {
            return markets;
        }
        content.sortBy(sortColumn, sortDescending);
        List<MarketAPI> ordered = new ArrayList<>();
        for (MarketTableRow row : content.getRows()) {
            ordered.add(row.getMarket());
        }
        return ordered;
    }

    private int calcNumberOfButtons(List<MarketAPI> markets, Size size) {
        float width = size.getWidth() - 210;
        int buttonsOnScreen = (int) Math.floor(width / 28f);
        int maxButtons = markets.size();
        return Math.min(buttonsOnScreen, maxButtons);
    }
}
