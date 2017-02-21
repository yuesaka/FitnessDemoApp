package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity lists the leaderboard based on the distance traveled by each other today.
 */
public class LeaderBoardActivity extends ActionBarActivity {
    private ListView leaderboardListView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        leaderboardListView = (ListView) findViewById(R.id.leader_board_list);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        List<Pair<String, Double>> leaderboardList = dbHelper.getLeaderboardList();
        List<String> leaderboardListString = new ArrayList<>();
        int place = 1;
        for (Pair<String, Double> pair : leaderboardList) {
            leaderboardListString.add(getString(R.string.leaderboard_entry, place, pair.first,
                    pair.second.toString(), Utility.formatDouble(pair.second),
                    Utility.formatDouble(pair.second * Utility.METER_TO_FEET_CONVERSION)));
            place++;
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        leaderboardListString);
        leaderboardListView.setAdapter(itemsAdapter);
    }
}
