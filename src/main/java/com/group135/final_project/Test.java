package com.group135.final_project;

import java.util.Arrays;
import java.util.List;

import com.group135.final_project.services.LODService;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;


public class Test {
    

    private static String str = 
    """ 
    1McMsnEElThX1knmY4oliG, 4q3ewBCX7sLwd24euuV69X, 0EFisYRi20PTADoJrifHrz, 1Xyo4u8uXC1ZmMpatF05PJ, 
    6fWVd57NKTalqvmjRd2t8Z, 6ASri4ePR7RlsvIQgWPJpS, 790FomKkXshlbRYZFtlgla, 66CXWjxzNUsdJxJ2JdwvnR, 
    66CXWjxzNUsdJxJ2JdwvnR, 1uNFoZAHBGtllmzznpCI3s, 6MPCFvOQv5cIGfw3jODMF0, 2hlmm7s2ICUX0LVIhVFlZQ, 
    4Gso3d4CscCijv0lmajZWs, 7rkW85dBwwrJtlHRDkJDAC, 4q3ewBCX7sLwd24euuV69X, 7ltDVBr6mKbRvohxheJ9h1, 
    7arQA31aZVS8yS6zUveWzb, 2tIP7SsRs7vjIcLrU85W8J, 3Nrfpe0tUJi4K4DXYWgMUX, 7tYKF4w9nC0nq9CsPZTHyP, 
    6qqNVTkY8uBg9cP3Jd7DAH, 6M2wZ9GZgrQXHCFfjv46we, 4r63FhuTkUYltbVAg5TQnk, 7iK8PXO48WeuP03g8YR51W""";
    
    public static void main(String[] args) throws Exception{
        var spotifyIds = Arrays.asList(str.split("\\s*,\\s*"));
        var res = LODService.fetchArtistInfo(spotifyIds);
        ResultSetFormatter.out(res);
    }
}
