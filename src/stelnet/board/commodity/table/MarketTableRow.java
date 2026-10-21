package stelnet.board.commodity.table;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import stelnet.util.StelnetHelper;
import uilib.TableContentRow;

public class MarketTableRow implements TableContentRow {

    private List<Object> elements = new LinkedList<>();

    @Getter
    private final MarketAPI market;

    @Getter
    private final int originalIndex;

    @Getter
    private final float price;

    @Getter
    private final int demandOrAvailability;

    @Getter
    private final int excessOrDeficit;

    @Getter
    private final String locationName;

    @Getter
    private final String systemName;

    @Getter
    private final float distance;

    public MarketTableRow(int i, float price, MarketAPI market, int demandOrAvailability, int excessOrDeficit) {
        String starSystemName = StelnetHelper.getStarSystemName(market.getStarSystem(), true);
        this.market = market;
        this.originalIndex = i;
        this.price = price;
        this.demandOrAvailability = demandOrAvailability;
        this.excessOrDeficit = excessOrDeficit;
        this.locationName = StelnetHelper.getMarketWithFactionName(market);
        this.systemName = starSystemName;
        this.distance = Misc.getDistanceToPlayerLY(market.getPrimaryEntity());
        addRowNumberCell(i);
        addDGSCreditsCell(price);
        addDGSCell(demandOrAvailability);
        addExcessDemandCell(excessOrDeficit);
        addCell(StelnetHelper.getFactionColor(market.getFaction()), StelnetHelper.getMarketWithFactionName(market));
        addCell(getClaimingFactionColor(market), starSystemName);
        addCell(Misc.getTextColor(), String.format("%.1f", distance));
    }

    /** Replaces the "#" cell text (cell 0 is the first of three elements: alignment, color, text). */
    public void setNumber(int number) {
        elements.set(2, number + ".");
    }

    public Object[] buildObjectArray() {
        return elements.toArray();
    }

    private void addRowNumberCell(Integer i) {
        addCell(Misc.getGrayColor(), i + ".");
    }

    private void addDGSCreditsCell(float value) {
        addCell(Misc.getTextColor(), Misc.getDGSCredits(value));
    }

    private void addDGSCell(int value) {
        addCell(Misc.getTextColor(), Misc.getWithDGS(value));
    }

    private void addExcessDemandCell(int value) {
        addCell(getExcessDemandColor(value), getExcessDemandValue(value));
    }

    private void addCell(Color color, Object element) {
        elements.add(Alignment.MID);
        elements.add(color);
        elements.add(element.toString());
    }

    private Color getClaimingFactionColor(MarketAPI market) {
        FactionAPI faction = Misc.getClaimingFaction(market.getPrimaryEntity());
        return StelnetHelper.getFactionColor(faction);
    }

    private String getExcessDemandValue(int excessDemand) {
        if (excessDemand > 0) {
            return Misc.getWithDGS(excessDemand);
        }
        if (excessDemand < 0) {
            return Misc.getWithDGS(-excessDemand);
        }
        return "---";
    }

    private Color getExcessDemandColor(int excessDemand) {
        if (excessDemand > 0) {
            return Misc.getPositiveHighlightColor();
        }
        if (excessDemand < 0) {
            return Misc.getNegativeHighlightColor();
        }
        return Misc.getGrayColor();
    }
}
