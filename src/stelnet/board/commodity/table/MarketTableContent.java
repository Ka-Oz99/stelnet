package stelnet.board.commodity.table;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import stelnet.board.commodity.CommodityL10n;
import stelnet.board.commodity.price.Price;
import stelnet.util.L10n;
import uilib.TableContent;

@RequiredArgsConstructor
@Getter
public abstract class MarketTableContent implements TableContent {

    protected final String commodityId;
    protected final List<MarketAPI> markets;
    protected final Price price;
    protected List<MarketTableRow> rows = new LinkedList<>();
    private int sortColumn = 0;
    private boolean sortDescending = false;

    /**
     * Sort by header column index (1 = first column after "#"). Column 0 keeps the original order.
     */
    public void sortBy(int column, boolean descending) {
        this.sortColumn = column;
        this.sortDescending = descending;
    }

    @Override
    public List<MarketTableRow> getRows() {
        List<MarketTableRow> sorted = new ArrayList<>(rows);
        if (sortColumn > 0) {
            Comparator<MarketTableRow> comparator = getComparator(sortColumn);
            if (sortDescending) {
                comparator = Collections.reverseOrder(comparator);
            }
            Collections.sort(sorted, comparator);
        }
        int i = 1;
        for (MarketTableRow row : sorted) {
            row.setNumber(i++);
        }
        return sorted;
    }

    private Comparator<MarketTableRow> getComparator(int column) {
        switch (column) {
            case 1:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return Float.compare(a.getPrice(), b.getPrice());
                    }
                };
            case 2:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return Integer.compare(a.getDemandOrAvailability(), b.getDemandOrAvailability());
                    }
                };
            case 3:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return Integer.compare(a.getExcessOrDeficit(), b.getExcessOrDeficit());
                    }
                };
            case 4:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return a.getLocationName().compareToIgnoreCase(b.getLocationName());
                    }
                };
            case 5:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return a.getSystemName().compareToIgnoreCase(b.getSystemName());
                    }
                };
            default:
                return new Comparator<MarketTableRow>() {
                    public int compare(MarketTableRow a, MarketTableRow b) {
                        return Float.compare(a.getDistance(), b.getDistance());
                    }
                };
        }
    }

    protected Object[] getHeader(float width, Enum<?> availableOrDemand, Enum<?> excessOrDeficit) {
        Object header[] = {
            "#",
            .05f * width,
            L10n.get(CommodityL10n.HEADER_PRICE),
            .1f * width,
            L10n.get(availableOrDemand),
            .11f * width,
            L10n.get(excessOrDeficit),
            .11f * width,
            L10n.get(CommodityL10n.HEADER_LOCATION),
            .32f * width,
            L10n.get(CommodityL10n.HEADER_SYSTEM),
            .21f * width,
            L10n.get(CommodityL10n.HEADER_DISTANCE),
            .1f * width,
        };
        return header;
    }

    protected void createRows() {
        rows.clear();
        int i = 1;
        for (MarketAPI market : markets) {
            MarketTableRow row = createRowData(i++, market);
            rows.add(row);
        }
    }

    protected CommodityOnMarketAPI getCommodityData(MarketAPI market) {
        return market.getCommodityData(commodityId);
    }

    protected abstract MarketTableRow createRowData(int i, MarketAPI market);
}
