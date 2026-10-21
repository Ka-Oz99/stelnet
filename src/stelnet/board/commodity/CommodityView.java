package stelnet.board.commodity;

import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import stelnet.board.commodity.view.board.CommodityViewFactory;
import stelnet.board.commodity.view.board.DeleteViewFactory;
import stelnet.board.commodity.view.board.IntelViewFactory;
import stelnet.board.commodity.view.board.TabViewFactory;
import uilib.Renderable;
import uilib.RenderableFactory;
import uilib.property.Size;

@RequiredArgsConstructor
public class CommodityView implements RenderableFactory {

    private final String commodityId;
    private final CommodityAction activeTab;
    private final IntelTracker intelTracker;
    private final int sortColumn;
    private final boolean sortDescending;

    @Override
    public List<Renderable> create(Size size) {
        List<Renderable> elements = new LinkedList<>();
        elements.addAll(new TabViewFactory(commodityId, activeTab, sortColumn, sortDescending).create(size));
        elements.addAll(new IntelViewFactory(commodityId, activeTab, intelTracker, sortColumn, sortDescending).create(size));
        elements.addAll(new CommodityViewFactory(commodityId).create(size));
        elements.addAll(new DeleteViewFactory(commodityId, intelTracker).create(size));
        return elements;
    }
}
