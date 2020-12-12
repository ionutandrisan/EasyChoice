package com.licenta.andrisan.easychoice.services;

import com.licenta.andrisan.easychoice.database.DatabaseConnection;
import com.licenta.andrisan.easychoice.payload.ReviewRequest;
import com.licenta.andrisan.easychoice.payload.WriteReviewRequest;
import oracle.jdbc.OracleTypes;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

@Service
public class ReviewService {

    public LinkedList<ReviewRequest> getHouseReviews(int houseID) throws SQLException, ClassNotFoundException {
        LinkedList<ReviewRequest> reviews = new LinkedList<>();

        String runOracleFunction = "{ ? = call get_house_reviews(?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.setInt(2, houseID);
            callableStatement.execute();

            ResultSet res = (ResultSet) callableStatement.getObject(1);
            while (res.next()) {
                ReviewRequest review = new ReviewRequest();
                review.setDate(res.getString("REVIEW_DATE").split(" ")[0]);
                review.setComment(res.getString("REVIEW_COMMENTS"));
                review.setLastName(res.getString("LAST_NAME"));
                review.setUserImage(res.getString("PHOTO"));
                review.setUserID(res.getInt("PERSON_ID"));
                review.setRating(res.getInt("RATING"));
                reviews.add(review);
            }

            res.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
        return reviews;

    }

    public void addNewReview(WriteReviewRequest reviewComment, int houseID, String email) throws SQLException, ClassNotFoundException {

        String runStoredProcedure = "{ call licenta_review_pkg.insert_review(?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, reviewComment.getReviewComment());
            callableStatement.setInt(2, reviewComment.getReviewRating());
            callableStatement.setString(3, email);
            callableStatement.setInt(4, houseID);
            callableStatement.execute();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();

    }

}
