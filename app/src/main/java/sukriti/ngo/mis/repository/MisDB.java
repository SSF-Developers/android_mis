package sukriti.ngo.mis.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.BwtConfig;
import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.BwtProfile;
import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.ClientRequest;
import sukriti.ngo.mis.repository.entity.CmsConfig;
import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.repository.entity.DailyFeedback;
import sukriti.ngo.mis.repository.entity.DailyUsage;
import sukriti.ngo.mis.repository.entity.DailyUsageCharge;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.OdsConfig;
import sukriti.ngo.mis.repository.entity.QuickAccess;
import sukriti.ngo.mis.repository.entity.ResetProfile;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.TicketProgress;
import sukriti.ngo.mis.repository.entity.UcemsConfig;
import sukriti.ngo.mis.repository.entity.UsageProfile;

@Database(entities = {Health.class, BwtHealth.class, AqiLumen.class, UcemsConfig.class,
        OdsConfig.class, CmsConfig.class, ClientRequest.class, BwtConfig.class,UsageProfile.class,
        ResetProfile.class, BwtProfile.class, DailyUsage.class, DailyFeedback.class,
        DailyUsageCharge.class, Complex.class, Cabin.class, QuickAccess.class, Ticket.class,
        TicketProgress.class},
        version = 3, exportSchema = false)
public abstract class MisDB extends RoomDatabase {
    private static MisDB INSTANCE;
    public abstract DataAccess dataDAO();

    public static MisDB getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MisDB.class, MisDB.class.getName())
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
