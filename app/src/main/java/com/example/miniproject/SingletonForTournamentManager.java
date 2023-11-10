package com.example.miniproject;

import java.util.ArrayList;
import java.util.List;

public class SingletonForTournamentManager {
    private static SingletonForTournamentManager tournamentManager;
    private static List<Tournament> tournamentList;
    //private static List<String> keywordsList;
    private SingletonForTournamentManager()
    {
        tournamentList = new ArrayList<Tournament>();
    }
    public static SingletonForTournamentManager getInstance()
    {
        if(tournamentManager == null)
            tournamentManager = new SingletonForTournamentManager();

        return tournamentManager;
    }
    public List<Tournament> getTournamentList()
    {
        return tournamentList;
    }
    public void setTournamentList(List<Tournament> tournamentList)
    {
        this.tournamentList.clear();
        this.tournamentList.addAll(tournamentList);
    }
    public Tournament getTournament(int index)
    {
        return index >= tournamentList.size() || index < 0 ? null : tournamentList.get(index);
    }
    public Tournament getTournament(String tournamentName)
    {
        for(int i = 0; i < tournamentList.size(); i++){
            if(tournamentList.get(i).name.equals(tournamentName))
                return tournamentList.get(i);
        }
        return null;
    }
}
