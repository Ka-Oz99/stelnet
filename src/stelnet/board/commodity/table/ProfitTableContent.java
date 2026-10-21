package stelnet.board.commodity.table;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import stelnet.board.commodity.CommodityL10n;
import stelnet.util.L10n;
import stelnet.util.StelnetHelper;
import uilib.TableContent;

@RequiredArgsConstructor
public class ProfitTableContent implements TableContent {

    public static int MAX_ROWS = 50;
    public static int MINIMUM_PROFIT_VALUE = 10000;

    private final List<MarketAPI> sellMarkets;
    private final List<MarketAPI> buyMarkets;
    private final String commodityId;
    private int sortColumn = 0;
    private boolean sortDescending = false;

    /**
     * Sort by header column index (1 = first column after "#"). Column 0 keeps the profit ranking.
     */
    public void sortBy(int column, boolean descending) {
        this.sortColumn = column;
        this.sortDescending = descending;
    }

    @Override
    public Object[] getHeaders(float width) {
        return new Object[] {
            "#",
            .05f * width,
            L10n.get(CommodityL10n.HEADER_PROFIT),
            .17f * width,
            L10n.get(CommodityL10n.HEADER_BUY_LOCATION),
            .22f * width,
            L10n.get(CommodityL10n.HEADER_BUY_EXPENSE),
            .12f * width,
            L10n.get(CommodityL10n.HEADER_SELL_LOCATION),
            .22f * width,
            L10n.get(CommodityL10n.HEADER_SELL_REVENUE),
            .12f * width,
            L10n.get(CommodityL10n.HEADER_TRIP),
            .1f * width,
        };
    }

    private static String cachedKey;
    private static long cachedAt;
    private static List<ProfitTableRow> cachedRows;
    private static final long CACHE_MILLIS = 15000;

    @Override
    public List<ProfitTableRow> getRows() {
        List<ProfitTableRow> top = new ArrayList<>(getTopRows());
        if (sortColumn > 0) {
            Comparator<ProfitTableRow> comparator = getComparator(sortColumn);
            if (sortDescending) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(top, comparator);
        }
        injectNumbers(top);
        return top;
    }

    /** The expensive part: every buy/sell market pair. Reused for a few seconds so clicking a header is instant. */
    private List<ProfitTableRow> getTopRows() {
        String key = commodityId + ":" + MAX_ROWS + ":" + MINIMUM_PROFIT_VALUE + ":" + ProfitTableRow.MINIMUM_QUANTITY;
        long now = System.currentTimeMillis();
        if (cachedRows == null || !key.equals(cachedKey) || now - cachedAt > CACHE_MILLIS) {
            List<ProfitTableRow> created = createRows();
            Collections.sort(created);
            int limit = Math.min(MAX_ROWS, created.size());
            cachedRows = new ArrayList<>(created.subList(0, limit));
            cachedKey = key;
            cachedAt = now;
        }
        return cachedRows;
    }

    private Comparator<ProfitTableRow> getComparator(int column) {
        switch (column) {
            case 1:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return Float.compare(a.getProfit(), b.getProfit());
                    }
                };
            case 2:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return StelnetHelper
                            .getMarketWithFactionName(a.getBuyMarket())
                            .compareToIgnoreCase(StelnetHelper.getMarketWithFactionName(b.getBuyMarket()));
                    }
                };
            case 3:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return Float.compare(a.getBuyExpense(), b.getBuyExpense());
                    }
                };
            case 4:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return StelnetHelper
                            .getMarketWithFactionName(a.getSellMarket())
                            .compareToIgnoreCase(StelnetHelper.getMarketWithFactionName(b.getSellMarket()));
                    }
                };
            case 5:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return Float.compare(a.getSellRevenue(), b.getSellRevenue());
                    }
                };
            default:
                return new Comparator<ProfitTableRow>() {
                    public int compare(ProfitTableRow a, ProfitTableRow b) {
                        return Float.compare(a.getDistance(), b.getDistance());
                    }
                };
        }
    }

    private List<ProfitTableRow> createRows() {
        List<ProfitTableRow> rows = new LinkedList<>();
        for (MarketAPI buyMarket : buyMarkets) {
            List<ProfitTableRow> sellMarketTableRows = createSellMarketRows(buyMarket, sellMarkets);
            rows.addAll(sellMarketTableRows);
        }
        return rows;
    }

    private List<ProfitTableRow> createSellMarketRows(MarketAPI buyMarket, List<MarketAPI> sellMarkets) {
        List<ProfitTableRow> rows = new LinkedList<>();
        for (MarketAPI sellMarket : sellMarkets) {
            ProfitTableRow row = new ProfitTableRow(buyMarket, sellMarket, commodityId);
            if (row.getProfit() >= MINIMUM_PROFIT_VALUE) {
                rows.add(row);
            }
        }
        return rows;
    }

    private void injectNumbers(List<ProfitTableRow> rows) {
        int i = 1;
        for (ProfitTableRow row : rows) {
            row.addNumber(i++);
        }
    }
}
