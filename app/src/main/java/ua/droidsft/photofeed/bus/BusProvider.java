package ua.droidsft.photofeed.bus;

import com.squareup.otto.Bus;

/**
 * Class provides Otto Event Bus.
 * Created by Vlad on 21.04.2016.
 */
public final class BusProvider {
    private static final Bus BUS = new MainThreadBus();

    public static Bus bus() {
        return BUS;
    }

    private BusProvider() {
    }
}
