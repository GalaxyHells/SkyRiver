package com.galaxyhells.model;

import java.util.List;
import java.util.Map;

public class ProfileData {
    public int activeProfile;
    public String name;
    public List<Profile> profiles;
    public long lastFetch; // Cache interno

    public static class Profile {
        public String profile_name;
        public DataModel data_model;
    }

    public static class DataModel {
        public BankModel bank_model;
        public SkillsModel skills_model;
    }

    public static class BankModel {
        public double purse;
    }

    public static class SkillsModel {
        public Map<String, Integer> level;
    }

    // Método auxiliar para pegar o perfil ativo com segurança
    public Profile getActive() {
        if (profiles != null && activeProfile < profiles.size()) {
            return profiles.get(activeProfile);
        }
        return null;
    }
}