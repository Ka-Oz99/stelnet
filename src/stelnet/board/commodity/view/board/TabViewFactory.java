package stelnet.board.commodity.view.board;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.lwjgl.input.Keyboard;
import stelnet.board.commodity.CommodityAction;
import stelnet.board.commodity.table.BuyTableContent;
import stelnet.board.commodity.table.ProfitTableContent;
import stelnet.board.commodity.table.SellTableContent;
import uilib.Button;
import uilib.Renderable;
import uilib.RenderableFactory;
import uilib.TabViewContainer;
import uilib.Table;
import uilib.TableContent;
import uilib.property.Size;

@RequiredArgsConstructor
public class TabViewFactory implements RenderableFactory {

    /** Fine tuning: stretches (>1) or shrinks (<1) the clickable titles to match the table columns. */
    private static final float HEADER_WIDTH_SCALE = 1.0163f;

    private final String commodityId;
    private final CommodityAction commodityAction;
    private final int sortColumn;
    private final boolean sortDescending;

    @Override
    public List<Renderable> create(Size size) {
        float width = size.getWidth() - 210;
        float height = size.getHeight();
        if (!isActive(CommodityAction.PROFIT)) {
            height -= 24;
        }
        TabViewContainer tabViewContainer = new TabViewContainer();
        tabViewContainer.setSize(new Size(width, height));
        addBuyTab(tabViewContainer, width);
        addSellTab(tabViewContainer, width);
        addProfitTab(tabViewContainer, width);
        return Collections.<Renderable>singletonList(tabViewContainer);
    }

    private void addBuyTab(TabViewContainer tabViewContainer, float width) {
        TabViewButton tabButton = getTabButton(CommodityAction.BUY, Keyboard.KEY_B);
        Table table = getBuyTable(width);
        boolean isActive = isActive(CommodityAction.BUY);
        tabViewContainer.addTab(tabButton, table, isActive);
    }

    private void addSellTab(TabViewContainer tabViewContainer, float width) {
        TabViewButton tabButton = getTabButton(CommodityAction.SELL, Keyboard.KEY_S);
        Table table = getSellTable(width);
        boolean isActive = isActive(CommodityAction.SELL);
        tabViewContainer.addTab(tabButton, table, isActive);
    }

    private void addProfitTab(TabViewContainer tabViewContainer, float width) {
        TabViewButton tabButton = getTabButton(CommodityAction.PROFIT, Keyboard.KEY_P);
        Table table = getProfitTable(width);
        boolean isActive = isActive(CommodityAction.PROFIT);
        tabViewContainer.addTab(tabButton, table, isActive);
    }

    private Table getBuyTable(float width) {
        List<MarketAPI> markets = CommodityAction.BUY.getMarkets(commodityId);
        BuyTableContent tableContent = new BuyTableContent(commodityId, markets);
        tableContent.sortBy(getSortColumn(CommodityAction.BUY), sortDescending);
        return createTable(width, tableContent);
    }

    private Table getSellTable(float width) {
        List<MarketAPI> markets = CommodityAction.SELL.getMarkets(commodityId);
        SellTableContent tableContent = new SellTableContent(commodityId, markets);
        tableContent.sortBy(getSortColumn(CommodityAction.SELL), sortDescending);
        return createTable(width, tableContent);
    }

    private Table getProfitTable(float width) {
        List<MarketAPI> sellMarkets = CommodityAction.PROFIT.getSellMarkets(commodityId);
        List<MarketAPI> buyMarkets = CommodityAction.PROFIT.getBuyMarkets(commodityId);
        ProfitTableContent tableContent = new ProfitTableContent(sellMarkets, buyMarkets, commodityId);
        tableContent.sortBy(getSortColumn(CommodityAction.PROFIT), sortDescending);
        return createTable(width, tableContent);
    }

    /** Sorting only applies to the tab that is currently shown. */
    private int getSortColumn(CommodityAction tab) {
        return isActive(tab) ? sortColumn : 0;
    }

    private Table createTable(float width, TableContent tableContent) {
        Table table = new Table(commodityId, width, 0, tableContent);
        table.setSortButtons(createSortButtons(width, tableContent));
        return table;
    }

    /**
     * Builds one clickable button per table header. Header array is made of (title, width) pairs, and the table
     * itself is given width - 24 to lay out its columns.
     */
    private List<Button> createSortButtons(float width, TableContent tableContent) {
        List<Button> buttons = new ArrayList<>();
        Object[] headers = tableContent.getHeaders((width - 24) * HEADER_WIDTH_SCALE);
        for (int i = 0; i + 1 < headers.length; i += 2) {
            int column = i / 2;
            String title = headers[i].toString();
            float columnWidth = ((Number) headers[i + 1]).floatValue();
            boolean isActive = column > 0 && column == sortColumn;
            buttons.add(new SortHeaderButton(title, columnWidth, column, isActive, sortDescending));
        }
        return buttons;
    }

    private TabViewButton getTabButton(CommodityAction currentTab, int keyboardShortcut) {
        return new TabViewButton(currentTab, commodityAction, keyboardShortcut);
    }

    private boolean isActive(CommodityAction currentAction) {
        return currentAction.equals(commodityAction);
    }
}
