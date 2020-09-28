package com.example.wordlist;

import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.Query;

public interface DataMuseService {
    //Mot clé de l'API utilisé
    @GET("sug?max=100")
    Call<List<WordFromAPI>> listSimilarWords(@Query("s") String querySimilarWords);
}
//Interface donnée dans la fiche