package sukriti.ngo.mis.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sukriti.ngo.mis.dataModel.dynamo_db.*;
import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.ThingGroupDetails;
import sukriti.ngo.mis.repository.data.FeedbackStatsData;
import sukriti.ngo.mis.repository.entity.UsageProfile;
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;
import sukriti.ngo.mis.ui.administration.data.UserAccessCount;
import sukriti.ngo.mis.ui.administration.data.UserAccessKey;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientList;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.ui.reports.data.SelectionData;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.ui.reports.data.UsageReportRawData;

public class Utilities {
    private static String tag = "CompletedAccessTree";
    private Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideKeypad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String capsWord(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static void disableEditText(EditText view) {
        view.setFocusableInTouchMode(false);
        view.setFocusable(false);
        view.setEnabled(false);
        view.setCursorVisible(false);
        view.setKeyListener(null);
    }

    public static void setError(EditText formView, TextView errorView, String Message) {
        errorView.setText(Message);
        formView.setBackgroundResource(R.drawable.text_border_error);
    }

    public static void clearError(EditText formView, TextView errorView) {
        errorView.setText("");
        formView.setBackgroundResource(R.drawable.text_border_selector);
    }

    public static ArrayList<String> getNameList(ArrayList<ThingGroupDetails> List) {
        ArrayList<String> nameList = new ArrayList<>();
        for (ThingGroupDetails item : List) {
            nameList.add(item.Name);
        }
        return nameList;
    }

    public static ArrayList<String> getNameList(List<ClientList> list) {
        ArrayList<String> nameList = new ArrayList<>();
        for (ClientList client : list) {
            nameList.add(client.getName());
        }
        return nameList;
    }

    public static int getStateIndex(String itemName, List<State> list) {

        int pointer = 0;
        int index = -1;
        for (State state : list) {

            if (state.getName().compareToIgnoreCase(itemName) == 0)
                index = pointer;
            pointer++;
        }
        return index;
    }

    public static int getDistrictIndex(String itemName, List<District> list) {
        int index = 0;
        for (District district : list) {
            if (district.getName().compareToIgnoreCase(itemName) == 0)
                return index;
            index++;
        }
        return -1;
    }

    public static int getCityIndex(String itemName, List<City> list) {
        int index = 0;
        for (City city : list) {
            if (city.getName().compareToIgnoreCase(itemName) == 0)
                return index;
            index++;
        }
        return -1;
    }

    public static String getInitials(String role) {
        String x = role.split(" ")[0].substring(0, 1).toUpperCase();
        String y = role.split(" ")[1].substring(0, 1).toUpperCase();
        return x + "." + y;
    }

    public static List<String> getNames(List<Complex> data) {
        List<String> names = new ArrayList<>();
        HashMap<String, String> clientComplexHash = new HashMap<>();

        for (Complex item : data) {
            names.add(item.getName());
            clientComplexHash.put(item.getClient(), item.getName());
        }

        for (String clientName : clientComplexHash.keySet()) {
            names.add(clientName + "_ALL");
        }


        for (String complexname : names)
            Log.i("__ClientRequest", complexname);

        return names;
    }

    public static List<String> getComplexNamesList(List<Complex> data) {
        List<String> names = new ArrayList<>();

        for (Complex item : data) {
            names.add(item.getName());
        }

        return names;
    }

    public static Integer convertToDbFeedback(String feedback) {
        try {
            return Integer.valueOf(feedback);
        } catch (NumberFormatException e) {
            if (feedback.compareToIgnoreCase("Not Given") == 0)
                return 0;
        }

        return -2;
    }

    public static Double convertToDbAmountCollected(String amountCollected) {
        return Double.valueOf(amountCollected);
    }

    public static String convertToDbCabinType(String shortThingName) {
        if (shortThingName.toLowerCase().contains("mwc"))
            return Nomenclature.CABIN_TYPE_MWC;
        else if (shortThingName.toLowerCase().contains("fwc"))
            return Nomenclature.CABIN_TYPE_FWC;
        else if (shortThingName.toLowerCase().contains("pwc"))
            return Nomenclature.CABIN_TYPE_PWC;
        else if (shortThingName.toLowerCase().contains("mur"))
            return Nomenclature.CABIN_TYPE_MUR;
        else
            return Nomenclature.CABIN_TYPE_NULL;
    }

    public static int convertToDbDuration(String duration) {
        return Integer.valueOf(duration);
    }

    public static String getDiplayAssigmentType(String assignmentType) {
        if (assignmentType.compareToIgnoreCase("on_create") == 0)
            return "Default";
        else
            return "Updated";
    }

    public static String getDisplayAmount(Float amount) {
        return NumberFormat.getNumberInstance().format(amount);
    }

    public static MemberDetailsData setAllComplexesSelected(MemberDetailsData memberDetailsData) {
        try {
            City completedCity = null;
            int stateIndex = 0;
            for (State state : memberDetailsData.userAccess.getStates()) {
                int districtIndex = 0;
                for (District district : state.getDistricts()) {
                    int cityIndex = 0;
                    for (City city : district.getCities()) {
                        for (Complex complex : city.getComplexes()) {
                            complex.setIsSelected(1);
                        }
                        cityIndex++;
                    }
                    districtIndex++;
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return memberDetailsData;
    }

    public static UserAccessCount getUserAccessCount(Country provisioningTree) {
        UserAccessCount count = new UserAccessCount(0, 0, 0, 0);

        int stateIndex = 0;
        int recursiveStateCount = 0;
        int activeStateCount = 0;
        for (Iterator<State> stateIterator = provisioningTree.getStates().iterator(); stateIterator.hasNext(); ) {
            State state = stateIterator.next();
            if (state.getRecursive() == 1) {
                count.setState(count.getState() + 1);
                recursiveStateCount++;
                activeStateCount++;
            } else {
                int districtIndex = 0;
                int recursiveDistrictCount = 0;
                int activeDistrictCount = 0;
                for (Iterator<District> districtIterator = state.getDistricts().iterator(); districtIterator.hasNext(); ) {
                    District district = districtIterator.next();
                    if (district.getRecursive() == 1) {
                        count.setDistrict(count.getDistrict() + 1);
                        activeDistrictCount++;
                        recursiveDistrictCount++;
                    } else {
                        int cityIndex = 0;
                        int recursiveCityCount = 0;
                        int activeCityCount = 0;
                        for (Iterator<City> cityIterator = district.getCities().iterator(); cityIterator.hasNext(); ) {
                            City city = cityIterator.next();
                            if (city.getRecursive() == 1) {
                                count.setCity(count.getCity() + 1);
                                recursiveCityCount++;
                                activeCityCount++;
                            } else {
                                int complexIndex = 0;
                                int selectedComplexCount = 0;
                                for (Iterator<Complex> complexIterator = city.getComplexes().iterator(); complexIterator.hasNext(); ) {
                                    Complex complex = complexIterator.next();
                                    if (complex.getIsSelected() == 0) {

                                    } else {
                                        selectedComplexCount++;
                                        count.setComplex(count.getComplex() + 1);
                                    }
                                    complexIndex++;
                                }
                                if (selectedComplexCount == 0) {

                                } else {
                                    activeCityCount++;
                                }
                            }
                            cityIndex++;
                        }
                        if (activeCityCount == 0) {

                        } else {
                            activeDistrictCount++;
                        }
                    }
                    districtIndex++;
                }
                if (activeDistrictCount == 0) {

                } else {
                    activeStateCount++;
                }
            }
            stateIndex++;
        }

        return count;
    }

    public static UserAccessCount getUserAccessCountRO(Country provisioningTree) {
        UserAccessCount count = new UserAccessCount(0, 0, 0, 0);

        int stateIndex = 0;
        int recursiveStateCount = 0;
        int activeStateCount = 0;
        for (Iterator<State> stateIterator = provisioningTree.getStates().iterator(); stateIterator.hasNext(); ) {
            State state = stateIterator.next();
            if (state.getRecursive() == 1) {
                count.setState(count.getState() + 1);
                recursiveStateCount++;
                activeStateCount++;
            } else {
                int districtIndex = 0;
                int recursiveDistrictCount = 0;
                int activeDistrictCount = 0;
                for (Iterator<District> districtIterator = state.getDistricts().iterator(); districtIterator.hasNext(); ) {
                    District district = districtIterator.next();
                    if (district.getRecursive() == 1) {
                        count.setDistrict(count.getDistrict() + 1);
                        activeDistrictCount++;
                        recursiveDistrictCount++;
                    } else {
                        int cityIndex = 0;
                        int recursiveCityCount = 0;
                        int activeCityCount = 0;
                        for (Iterator<City> cityIterator = district.getCities().iterator(); cityIterator.hasNext(); ) {
                            City city = cityIterator.next();
                            if (city.getRecursive() == 1) {
                                count.setCity(count.getCity() + 1);
                                recursiveCityCount++;
                                activeCityCount++;
                            } else {
                                int complexIndex = 0;
                                int selectedComplexCount = 0;
                                for (Iterator<Complex> complexIterator = city.getComplexes().iterator(); complexIterator.hasNext(); ) {
                                    Complex complex = complexIterator.next();
                                        selectedComplexCount++;
                                        count.setComplex(count.getComplex() + 1);
                                    complexIndex++;
                                }
                                if (selectedComplexCount == 0) {

                                } else {
                                    activeCityCount++;
                                }
                            }
                            cityIndex++;
                        }
                        if (activeCityCount == 0) {

                        } else {
                            activeDistrictCount++;
                        }
                    }
                    districtIndex++;
                }
                if (activeDistrictCount == 0) {

                } else {
                    activeStateCount++;
                }
            }
            stateIndex++;
        }

        return count;
    }

    public static Country getTrimmedAccessTree(Country provisioningTree) {
        ArrayList<TreeEdge> emptyNodes = new ArrayList<>();
        int stateIndex = 0;
        int recursiveStateCount = 0;
        int activeStateCount = 0;

        for (Iterator<State> stateIterator = provisioningTree.getStates().iterator(); stateIterator.hasNext(); ) {
            State state = stateIterator.next();
            if (state.getRecursive() == 1) {
                state.setDistricts(null);
                recursiveStateCount++;
                activeStateCount++;
            } else {
                int districtIndex = 0;
                int recursiveDistrictCount = 0;
                int activeDistrictCount = 0;
                for (Iterator<District> districtIterator = state.getDistricts().iterator(); districtIterator.hasNext(); ) {
                    District district = districtIterator.next();
                    if (district.getRecursive() == 1) {
                        district.setCities(null);
                        activeDistrictCount++;
                        recursiveDistrictCount++;
                    } else {
                        int cityIndex = 0;
                        int recursiveCityCount = 0;
                        int activeCityCount = 0;
                        for (Iterator<City> cityIterator = district.getCities().iterator(); cityIterator.hasNext(); ) {
                            City city = cityIterator.next();
                            if (city.getRecursive() == 1) {
                                city.setComplexes(null);
                                recursiveCityCount++;
                                activeCityCount++;
                            } else {
                                int complexIndex = 0;
                                int selectedComplexCount = 0;
                                for (Iterator<Complex> complexIterator = city.getComplexes().iterator(); complexIterator.hasNext(); ) {
                                    Complex complex = complexIterator.next();
                                    if (complex.getIsSelected() == 0) {
                                        //Remove Complex
                                        emptyNodes.add(new TreeEdge(stateIndex, districtIndex, cityIndex, complexIndex));
                                        complexIterator.remove();
                                    } else {
                                        selectedComplexCount++;
                                    }
                                    complexIndex++;
                                }
                                if (selectedComplexCount == 0) {
                                    //Remove City
                                    emptyNodes.add(new TreeEdge(stateIndex, districtIndex, cityIndex));
                                    cityIterator.remove();
                                } else {
                                    activeCityCount++;
                                }
                            }
                            cityIndex++;
                        }
                        if (activeCityCount == 0) {
                            //Remove District
                            emptyNodes.add(new TreeEdge(stateIndex, districtIndex));
                            districtIterator.remove();
                        } else {
                            activeDistrictCount++;
                        }
                    }
                    districtIndex++;
                }
                if (activeDistrictCount == 0) {
                    //Remove State
                    emptyNodes.add(new TreeEdge(stateIndex));
                    stateIterator.remove();
                } else {
                    activeStateCount++;
                }
            }
            stateIndex++;
        }

        for (TreeEdge edge : emptyNodes) {
            Log.i(tag, edge.getStateIndex() + "," + edge.getDistrictIndex() + "," + edge.getCityIndex() + "," + edge.getComplexIndex());
        }

        int stateRemovals = 0;
        int districtRemovals = 0;
        int cityRemovals = 0;
        int complexRemovals = 0;
        for (TreeEdge edge : emptyNodes) {
            if (edge.getDistrictIndex() == -1) {
                //Remove State
                stateRemovals++;
            } else if (edge.getCityIndex() == -1) {
                //Remove District
                districtRemovals++;
            } else if (edge.getComplexIndex() == -1) {
                //Remove City
                cityRemovals++;
            } else {
                //Remove Complex
                complexRemovals++;
            }
        }

        Log.i(tag, stateRemovals + "," + districtRemovals + "," + cityRemovals + "," + complexRemovals);

        return provisioningTree;
    }

    public static Country getTrimmedClientSuperAdminAccessTree(Country provisioningTree) {
        ArrayList<TreeEdge> emptyNodes = new ArrayList<>();
        int stateIndex = 0;
        int recursiveStateCount = 0;
        int activeStateCount = 0;

        for (Iterator<State> stateIterator = provisioningTree.getStates().iterator(); stateIterator.hasNext(); ) {
            State state = stateIterator.next();
            if (state.getRecursive() == 1) {
                state.setDistricts(null);
                recursiveStateCount++;
                activeStateCount++;
            } else {
                int districtIndex = 0;
                int recursiveDistrictCount = 0;
                int activeDistrictCount = 0;
                for (Iterator<District> districtIterator = state.getDistricts().iterator(); districtIterator.hasNext(); ) {
                    District district = districtIterator.next();
                    if (district.getRecursive() == 1) {
                        district.setCities(null);
                        activeDistrictCount++;
                        recursiveDistrictCount++;
                    } else {
                        int cityIndex = 0;
                        int recursiveCityCount = 0;
                        int activeCityCount = 0;
                        for (Iterator<City> cityIterator = district.getCities().iterator(); cityIterator.hasNext(); ) {
                            City city = cityIterator.next();
                            if (city.getRecursive() == 1) {
                                city.setComplexes(null);
                                recursiveCityCount++;
                                activeCityCount++;
                            } else {
                                int complexIndex = 0;
                                int selectedComplexCount = 0;
                                for (Iterator<Complex> complexIterator = city.getComplexes().iterator(); complexIterator.hasNext(); ) {
                                    Complex complex = complexIterator.next();
                                    selectedComplexCount++;
                                    complexIndex++;
                                }
                                if (selectedComplexCount == 0) {
                                    //Remove City
                                    emptyNodes.add(new TreeEdge(stateIndex, districtIndex, cityIndex));
                                    cityIterator.remove();
                                } else {
                                    activeCityCount++;
                                }
                            }
                            cityIndex++;
                        }
                        if (activeCityCount == 0) {
                            //Remove District
                            emptyNodes.add(new TreeEdge(stateIndex, districtIndex));
                            districtIterator.remove();
                        } else {
                            activeDistrictCount++;
                        }
                    }
                    districtIndex++;
                }
                if (activeDistrictCount == 0) {
                    //Remove State
                    emptyNodes.add(new TreeEdge(stateIndex));
                    stateIterator.remove();
                } else {
                    activeStateCount++;
                }
            }
            stateIndex++;
        }

//        for (TreeEdge edge : emptyNodes) {
//            Log.i(tag, edge.getStateIndex()+","+edge.getDistrictIndex()+","+edge.getCityIndex()+","+edge.getComplexIndex());
//        }
//
//        int stateRemovals = 0;
//        int districtRemovals = 0;
//        int cityRemovals = 0;
//        int complexRemovals = 0;
//        for (TreeEdge edge : emptyNodes) {
//            if (edge.getDistrictIndex() == -1) {
//                //Remove State
//                stateRemovals++;
//            } else if (edge.getCityIndex() == -1) {
//                //Remove District
//                districtRemovals++;
//            } else if (edge.getComplexIndex() == -1) {
//                //Remove City
//                cityRemovals++;
//            } else {
//                //Remove Complex
//                complexRemovals++;
//            }
//        }
//
//        Log.i(tag, stateRemovals+","+districtRemovals+","+cityRemovals+","+complexRemovals);

        return provisioningTree;
    }


    public static Country getTrimmedDisplayAccessTree(Country provisioningTree) {
        ArrayList<TreeEdge> emptyNodes = new ArrayList<>();
        int stateIndex = 0;
        int recursiveStateCount = 0;
        int activeStateCount = 0;

        for (Iterator<State> stateIterator = provisioningTree.getStates().iterator(); stateIterator.hasNext(); ) {
            State state = stateIterator.next();
            {
                int districtIndex = 0;
                int recursiveDistrictCount = 0;
                int activeDistrictCount = 0;
                Log.i("accessTree", "getTrimmedDisplayAccessTree hasNext: state.getDistricts().iterator(); districtIterator.hasNext()");
                for (Iterator<District> districtIterator = state.getDistricts().iterator(); districtIterator.hasNext(); ) {
                    District district = districtIterator.next();
                    {
                        int cityIndex = 0;
                        int recursiveCityCount = 0;
                        int activeCityCount = 0;
                        for (Iterator<City> cityIterator = district.getCities().iterator(); cityIterator.hasNext(); ) {
                            City city = cityIterator.next();
                            {
                                int complexIndex = 0;
                                int selectedComplexCount = 0;
                                for (Iterator<Complex> complexIterator = city.getComplexes().iterator(); complexIterator.hasNext(); ) {
                                    Complex complex = complexIterator.next();
                                    //provisioningTree.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().get(cityIndex).getComplexes().get(complexIndex).setIsSelected(1);
                                    complex.setIsSelected(1);
                                    selectedComplexCount++;
                                    complexIndex++;
                                }
                                if (selectedComplexCount == 0) {
                                    //Remove City
                                    emptyNodes.add(new TreeEdge(stateIndex, districtIndex, cityIndex));
                                    cityIterator.remove();
                                } else {
                                    activeCityCount++;
                                }
                            }
                            cityIndex++;
                        }
                        if (activeCityCount == 0) {
                            //Remove District
                            emptyNodes.add(new TreeEdge(stateIndex, districtIndex));
                            districtIterator.remove();
                        } else {
                            activeDistrictCount++;
                        }
                    }
                    districtIndex++;
                }
                if (activeDistrictCount == 0) {
                    //Remove State
                    emptyNodes.add(new TreeEdge(stateIndex));
                    stateIterator.remove();
                } else {
                    activeStateCount++;
                }
            }
            stateIndex++;
        }
        return provisioningTree;
    }

    public static Country getCompletedAccessTree(Country provisioningTree, Country adminAccessTree, Country userAccessTree) {

        Country completedAccessTree = new Country();
        int stateIndex = 0;
        if (adminAccessTree.getRecursive() == 1) {
            completedAccessTree = getCompleteCountryTree(provisioningTree);
        } else {
            for (State state : adminAccessTree.getStates()) {
                if (state.getRecursive() == 1) {
                    try {
                        completedAccessTree.getStates().set(stateIndex, getCompleteStateTree(state, provisioningTree));
                    } catch (IndexOutOfBoundsException e) {
                        completedAccessTree.getStates().add(getCompleteStateTree(state, provisioningTree));
                    }

                } else {
                    try {
                        completedAccessTree.getStates().set(stateIndex, new State(state));
                    } catch (IndexOutOfBoundsException e) {
                        completedAccessTree.getStates().add(new State(state));
                    }
                    int districtIndex = 0;
                    for (District district : state.getDistricts()) {
                        if (district.getRecursive() == 1) {
                            try {
                                completedAccessTree.getStates().get(stateIndex).getDistricts().set(districtIndex, getCompleteDistrictTree(district, provisioningTree));
                            } catch (IndexOutOfBoundsException e) {
                                completedAccessTree.getStates().get(stateIndex).getDistricts().add(getCompleteDistrictTree(district, provisioningTree));
                            }
                        } else {
                            completedAccessTree.getStates().get(stateIndex).getDistricts().set(districtIndex, new District(district));
                            int cityIndex = 0;
                            for (City city : district.getCities()) {
                                if (city.getRecursive() == 1) {
                                    try {
                                        completedAccessTree.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().set(cityIndex, getCompleteCityTree(city, provisioningTree));
                                    } catch (IndexOutOfBoundsException e) {
                                        completedAccessTree.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().add(getCompleteCityTree(city, provisioningTree));
                                    }
                                } else {
                                    completedAccessTree.getStates().get(stateIndex).getDistricts().get(districtIndex).getCities().set(cityIndex, new City(city));
                                }
                                cityIndex++;
                            }
                        }
                        districtIndex++;
                    }
                }
                stateIndex++;
            }
        }

        if (userAccessTree != null) {
            HashMap<String, TreeEdge> activeAccess = getActiveAccessPoints(userAccessTree);
            return setActiveAccessPoints(completedAccessTree, activeAccess);
        } else {
            return completedAccessTree;
        }
    }

    private static HashMap<String, TreeEdge> getActiveAccessPoints(Country accessTree) {
        HashMap<String, TreeEdge> activeAccess = new HashMap<>();
        try {
            int stateIndex = 0;
            for (State state : accessTree.getStates()) {
                if (state.getRecursive() == 1)
                    activeAccess.put(state.getName(), new TreeEdge(stateIndex));
                int districtIndex = 0;
                for (District district : state.getDistricts()) {
                    if (district.getRecursive() == 1)
                        activeAccess.put(district.getName(), new TreeEdge(stateIndex, districtIndex));
                    int cityIndex = 0;
                    for (City city : district.getCities()) {
                        if (city.getRecursive() == 1)
                            activeAccess.put(city.getName(), new TreeEdge(stateIndex, districtIndex, cityIndex));
                        int complexIndex = 0;
                        for (Complex complex : city.getComplexes()) {
                            if (complex.getIsSelected() == 1)
                                activeAccess.put(complex.getName(), new TreeEdge(stateIndex, districtIndex, cityIndex, complexIndex));
                            complexIndex++;
                        }
                        cityIndex++;
                    }
                    districtIndex++;
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            //accessTree Empty
            e.printStackTrace();
        }


        return activeAccess;
    }

    private static Country setActiveAccessPoints(Country accessTree, HashMap<String, TreeEdge> activeAccess) {
        int stateIndex = 0;
        for (State state : accessTree.getStates()) {
            if (activeAccess.get(state.getName()) != null)
                state.setRecursive(1);
            int districtIndex = 0;
            for (District district : state.getDistricts()) {
                if (activeAccess.get(district.getName()) != null)
                    district.setRecursive(1);
                int cityIndex = 0;
                for (City city : district.getCities()) {
                    if (activeAccess.get(city.getName()) != null)
                        city.setRecursive(1);
                    int complexIndex = 0;
                    for (Complex complex : city.getComplexes()) {
                        if (activeAccess.get(complex.getName()) != null)
                            complex.setIsSelected(1);
                        complexIndex++;
                    }
                    cityIndex++;
                }
                districtIndex++;
            }
            stateIndex++;
        }

        return accessTree;
    }

    private static Country getCompleteCountryTree(Country provisioningTree) {
        Country completedCountry = new Country(provisioningTree);
        return completedCountry;
    }

    private static State getCompleteStateTree(State selectedState, Country provisioningTree) {
        State completedState = null;
        int stateIndex = 0;
        for (State state : provisioningTree.getStates()) {
            if (state.getName().compareToIgnoreCase(selectedState.getName()) == 0) {
                Log.i(tag, "recursive state access");
                completedState = new State(state);
            }
            stateIndex++;
        }
        return completedState;
    }

    private static District getCompleteDistrictTree(District selectedDstrict, Country provisioningTree) {
        District completedDistrict = null;
        int stateIndex = 0;
        for (State state : provisioningTree.getStates()) {
            int districtIndex = 0;
            for (District district : state.getDistricts()) {
                if (district.getName().compareToIgnoreCase(selectedDstrict.getName()) == 0)
                    completedDistrict = new District(district);
                districtIndex++;
            }
            stateIndex++;
        }

        return completedDistrict;
    }

    private static City getCompleteCityTree(City selectedCity, Country provisioningTree) {
        City completedCity = null;
        int stateIndex = 0;
        for (State state : provisioningTree.getStates()) {
            int districtIndex = 0;
            for (District district : state.getDistricts()) {
                int cityIndex = 0;
                for (City city : district.getCities()) {
                    if (city.getName().compareToIgnoreCase(selectedCity.getName()) == 0)
                        completedCity = new City(city);
                    cityIndex++;
                }
                districtIndex++;
            }
            stateIndex++;
        }

        return completedCity;
    }

    private static HashMap<String, TreeEdge> filterClientSuperAdminAccessTree(Country accessTree, String Client) {
        HashMap<String, TreeEdge> activeAccess = new HashMap<>();
        try {
            int stateIndex = 0;
            for (State state : accessTree.getStates()) {
                if (state.getRecursive() == 1)
                    activeAccess.put(state.getName(), new TreeEdge(stateIndex));
                int districtIndex = 0;
                for (District district : state.getDistricts()) {
                    if (district.getRecursive() == 1)
                        activeAccess.put(district.getName(), new TreeEdge(stateIndex, districtIndex));
                    int cityIndex = 0;
                    for (City city : district.getCities()) {
                        if (city.getRecursive() == 1)
                            activeAccess.put(city.getName(), new TreeEdge(stateIndex, districtIndex, cityIndex));
                        int complexIndex = 0;
                        for (Complex complex : city.getComplexes()) {
                            if (complex.getClient().compareToIgnoreCase(Client) == 0) {

                            }
                            complexIndex++;
                        }
                        cityIndex++;
                    }
                    districtIndex++;
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            //accessTree Empty
            e.printStackTrace();
        }


        return activeAccess;
    }

    public static List<Complex> getAccessibleComplexList(Country accessTree) {
        List<Complex> complexList = new ArrayList<>();
        try {
            int stateIndex = 0;
            for (State state : accessTree.getStates()) {
                int districtIndex = 0;
                for (District district : state.getDistricts()) {
                    int cityIndex = 0;
                    for (City city : district.getCities()) {
                        int complexIndex = 0;
                        for (Complex complex : city.getComplexes()) {
                            Log.i("__ClientRequest", complex.getClient() + ", " + complex.getName());
                            complexList.add(complex);
                            complexIndex++;
                        }
                        cityIndex++;
                    }
                    districtIndex++;
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            //accessTree Empty
            e.printStackTrace();
        }
        Log.i("__getAccessibleComplex", "complexList: " + new Gson().toJson(complexList));
        return complexList;
    }

    public static List<UserAccessKey> getUserAccessKeys(Country accessTree) {
        List<UserAccessKey> userAccessKeys = new ArrayList<>();
        UserAccessKey accessKey;
        try {
            if(accessTree.getRecursive() == 1){
                userAccessKeys.add(new UserAccessKey(accessTree.getName(),"Country"));
            }
            int stateIndex = 0;
            for (State state : accessTree.getStates()) {
                if(state.getRecursive() == 1){
                    userAccessKeys.add(new UserAccessKey(state.getCode(),"State"));
                }
                int districtIndex = 0;
                for (District district : state.getDistricts()) {
                    if(district.getRecursive() == 1){
                        userAccessKeys.add(new UserAccessKey(district.getCode(),"District"));
                    }
                    int cityIndex = 0;
                    for (City city : district.getCities()) {
                        if(city.getRecursive() == 1){
                            userAccessKeys.add(new UserAccessKey(city.getCode(),"City"));
                        }
                        int complexIndex = 0;
                        for (Complex complex : city.getComplexes()) {
                            if(complex.getIsSelected() == 1){
                                userAccessKeys.add(new UserAccessKey(complex.getUuid(),"Complex"));
                            }
                            Log.i("__ClientRequest", complex.getClient() + ", " + complex.getName());
                            complexIndex++;
                        }
                        cityIndex++;
                    }
                    districtIndex++;
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            //accessTree Empty
            e.printStackTrace();
        }
        Log.i("__getAccessibleComplex", "complexList: " + new Gson().toJson(userAccessKeys));
        return userAccessKeys;
    }

    public static JSONObject getIotPolicyDocument(Country accessTree, UserProfile.Companion.UserRole role) {
        JSONObject policyDoc = new JSONObject();
        JSONArray policyStatements = new JSONArray();
        JSONObject statement = new JSONObject();
        JSONArray statementAction = new JSONArray();
        JSONArray statementResource = new JSONArray();
        try {

            //iot:connect
            statement = new JSONObject();
            statementAction = new JSONArray();
            statementResource = new JSONArray();
            statementResource.put("arn:aws:iot:ap-south-1:142770131582:client/${cognito-identity.amazonaws.com:sub}");
            statementAction.put("iot:connect");
            statement.put("Effect", "Allow");
            statement.put("Action", statementAction);
            statement.put("Resource", statementResource);
            policyStatements.put(statement);

            //iot:publish
            if (UserProfile.Companion.hasWriteAccess(role)) {
                statement = new JSONObject();
                statementAction = new JSONArray();
                statementResource = new JSONArray();
                for (String item : getPublishResources(accessTree))
                    statementResource.put(item);
                statementAction.put("iot:publish");
                statement.put("Effect", "Allow");
                statement.put("Action", statementAction);
                statement.put("Resource", statementResource);
                policyStatements.put(statement);
            }

            //iot:Subscribe
            statement = new JSONObject();
            statementAction = new JSONArray();
            statementAction.put("iot:Subscribe");
            statementResource = new JSONArray();
            statementResource.put("arn:aws:iot:ap-south-1:142770131582:topicfilter/*");
            statement.put("Effect", "Allow");
            statement.put("Action", statementAction);
            statement.put("Resource", statementResource);
            policyStatements.put(statement);

            //iot:Receive
            statement = new JSONObject();
            statementAction = new JSONArray();
            statementAction.put("iot:Receive");
            statementResource = new JSONArray();
            statementResource.put("arn:aws:iot:ap-south-1:142770131582:topicfilter/*");
            statement.put("Effect", "Allow");
            statement.put("Action", statementAction);
            statement.put("Resource", statementResource);
            policyStatements.put(statement);

            policyDoc.put("Version", "2012-10-17");
            policyDoc.put("Statement", policyStatements);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return policyDoc;
    }

    private static ArrayList<String> getPublishResources(Country accessTree) {
        ArrayList<String> publishResources = new ArrayList<>();
        String prefix = "arn:aws:iot:ap-south-1:142770131582:topicfilter/TOILETS/";
        int stateIndex = 0;
        for (State state : accessTree.getStates()) {
            if (state.getRecursive() == 1)
                publishResources.add(prefix + state.getCode() + "/*");
            int districtIndex = 0;
            for (District district : state.getDistricts()) {
                if (district.getRecursive() == 1)
                    publishResources.add(prefix + state.getCode() + "/" + district.getCode() + "/*");
                int cityIndex = 0;
                for (City city : district.getCities()) {
                    if (city.getRecursive() == 1)
                        publishResources.add(prefix + state.getCode() + "/" + district.getCode() + "/" + city.getCode() + "/*");
                    int complexIndex = 0;
                    for (Complex complex : city.getComplexes()) {
                        if (complex.getIsSelected() == 1)
                            publishResources.add(prefix + state.getCode() + "/" + district.getCode() + "/" + city.getCode() + "/" + complex.getClient() + "/" + complex.getName() + "/*");
                        complexIndex++;
                    }
                    cityIndex++;
                }
                districtIndex++;
            }
            stateIndex++;
        }

        return publishResources;
    }

    public static String getIotPolicyName(MemberDetailsData memberDetailsData) {
        String userName = memberDetailsData.cognitoUser.getUserName();
        String client = memberDetailsData.cognitoUser.getClient();
        if (client.isEmpty())
            client = "Sukriti";
        return "mis-" + client + "-" + userName;
    }

    public static String getIotPolicyName(UserProfile userProfile) {
        String userName = userProfile.getUser().getUserName();
        String client = userProfile.getOrganisation().getClient();
        if (client.isEmpty())
            client = "Sukriti";
        return "mis-" + client + "-" + userName;
    }

    public static SelectionData getSelectionData(Country accessTree) {
        ArrayList<String> states = new ArrayList<>();
        ArrayList<String> districts = new ArrayList<>();
        ArrayList<String> cities = new ArrayList<>();
        ArrayList<String> complexes = new ArrayList<>();

        int stateIndex = 0;
        for (State state : accessTree.getStates()) {
            if (state.getRecursive() == 1)
                states.add(state.getCode());
            int districtIndex = 0;
            for (District district : state.getDistricts()) {
                if (district.getRecursive() == 1)
                    districts.add(district.getName());
                int cityIndex = 0;
                for (City city : district.getCities()) {
                    if (city.getRecursive() == 1)
                        cities.add(city.getName());
                    int complexIndex = 0;
                    for (Complex complex : city.getComplexes()) {
                        if (complex.getIsSelected() == 1)
                            complexes.add(complex.getName());
                        complexIndex++;
                    }
                    cityIndex++;
                }
                districtIndex++;
            }
            stateIndex++;
        }

        return new SelectionData(states, districts, cities, complexes);
    }
    public static ArrayList<Complex> getSelectedComplexes(Country accessTree) {
        ArrayList<Complex> selectedComplexes = new ArrayList<>();
        try {
            int stateIndex = 0;
            for (State state : accessTree.getStates()) {
                int districtIndex = 0;
                if (state.getRecursive() == 1) {
                    for (District district : state.getDistricts()) {
                        int cityIndex = 0;
                        for (City city : district.getCities()) {
                            int complexIndex = 0;
                            for (Complex complex : city.getComplexes()) {
                                complexIndex++;
                                selectedComplexes.add(complex);
                            }
                            cityIndex++;
                        }
                        districtIndex++;
                    }
                } else {
                    for (District district : state.getDistricts()) {
                        int cityIndex = 0;
                        if (district.getRecursive() == 1) {
                            for (City city : district.getCities()) {
                                int complexIndex = 0;
                                for (Complex complex : city.getComplexes()) {
                                    complexIndex++;
                                    selectedComplexes.add(complex);
                                }
                                cityIndex++;
                            }
                        } else {
                            for (City city : district.getCities()) {
                                int complexIndex = 0;
                                if (city.getRecursive() == 1) {
                                    for (Complex complex : city.getComplexes()) {
                                        complexIndex++;
                                        selectedComplexes.add(complex);
                                    }
                                } else {
                                    for (Complex complex : city.getComplexes()) {
                                        complexIndex++;
                                        if (complex.getIsSelected() == 1)
                                            selectedComplexes.add(complex);
                                    }
                                }
                                cityIndex++;
                            }
                        }
                        districtIndex++;
                    }
                }
                stateIndex++;
            }
        } catch (NullPointerException e) {
            //accessTree Empty
            e.printStackTrace();
        }

        return selectedComplexes;
    }

    public static String getTimeDifference(long lastSynTimestamp) {
        String displayStr = "";
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long diff = currentTime - lastSynTimestamp;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        Log.i("_lastSync", "currentTime" + currentTime + ", lastSynTimestamp" + lastSynTimestamp);
        Log.i("_lastSync", "diff" + diff);

        //        Aug QA : rahul karn
        if(lastSynTimestamp == 0){
            displayStr="Not synced yet";
        }
        else if (seconds < 120) {
            displayStr = "Few seconds ago.";
        } else if (minutes < 120) {
            displayStr = minutes + " minutes ago";
        } else if (hours < 48) {
            displayStr = hours + " hours ago";
        } else {
            displayStr = days + " days ago";
        }

        return displayStr;
    }

    public static List<UsageProfile> getUsageProfileSubset(List<UsageProfile> data, String cabinType) {
        List<UsageProfile> subSet = new ArrayList<>();
        for (UsageProfile item : data) {
            if (Nomenclature.getCabinType(item.getSHORT_THING_NAME()).compareToIgnoreCase(cabinType) == 0)
                subSet.add(item);
        }

        return subSet;
    }

    public static List<UsageReportRawData> getUsageReportRawData(UsageReportData data) {
        List<UsageReportRawData> rawData = new ArrayList<>();
        rawData.add(new UsageReportRawData(Nomenclature.CABIN_TYPE_MWC,
                0,
                data.getUsageSummary().getMwc(),
                data.getFeedbackSummary().getMwc().averageFeedback,
                data.getCollectionSummary().getMale()));
        rawData.add(new UsageReportRawData(Nomenclature.CABIN_TYPE_FWC,
                0,
                data.getUsageSummary().getFwc(),
                data.getFeedbackSummary().getFwc().averageFeedback,
                data.getCollectionSummary().getFemale()));
        rawData.add(new UsageReportRawData(Nomenclature.CABIN_TYPE_PWC,
                0,
                data.getUsageSummary().getPwc(),
                data.getFeedbackSummary().getPwc().averageFeedback,
                data.getCollectionSummary().getPd()));
        rawData.add(new UsageReportRawData(Nomenclature.CABIN_TYPE_MUR,
                0,
                data.getUsageSummary().getMur(),
                data.getFeedbackSummary().getMur().averageFeedback,
                data.getCollectionSummary().getMur()));

//        rawData.add(new UsageReportRawData("Total",
//                rawData.get(0).getCabinCount()+rawData.get(1).getCabinCount()+rawData.get(2).getCabinCount()+rawData.get(3).getCabinCount(),
//                rawData.get(0).getUsage()+rawData.get(1).getUsage()+rawData.get(2).getUsage()+rawData.get(3).getUsage(),
//                getComplexFeedback(data.getFeedbackSummary()),
//                rawData.get(0).getCollection()+rawData.get(1).getCollection()+rawData.get(2).getCollection()+rawData.get(3).getCollection()));

        return rawData;
    }

    public static String convertEpochTStoDate(long unixSeconds) {
//        long unixSeconds = 1633072800L;

        // Convert seconds to LocalDateTime
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());


            // Format the date time in a readable way
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            Log.i( "UnixTime", "Formatted Date Time: " + formattedDateTime);
            return formattedDateTime;
        }
        return "";
    }

    private static float getComplexFeedback(FeedbackStatsData feedbackSummary) {
        float totalFeedback = feedbackSummary.getMwc().totalFeedback + feedbackSummary.getFwc().totalFeedback +
                feedbackSummary.getPwc().totalFeedback + feedbackSummary.getMur().totalFeedback;
        float totalUserCount = feedbackSummary.getMwc().userCount + feedbackSummary.getFwc().userCount +
                feedbackSummary.getPwc().userCount + feedbackSummary.getMur().userCount;

        if(totalUserCount == 0)
            return 0;
        return totalFeedback/totalUserCount;
    }

    public static boolean isTabletDevice(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        int screenSize = screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        return (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }
}
