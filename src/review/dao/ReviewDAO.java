package review.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jdbc.JDBC;
import review.dto.ReviewDTO;

public class ReviewDAO {
	private Connection conn = null;
	PreparedStatement pstmt = null;

	// review_id는 oralce sequence로 채운다
	public void insertReview(ReviewDTO reviewDTO) {
		String insertSQL = "INSERT INTO reviews (review_id, review_content, rating, write_Time, restaurant_id, user_id) VALUES (reviews_seq.NEXTVAL,?, ?,sysdate,?,?)";
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, reviewDTO.getContent());
			pstmt.setInt(2, reviewDTO.getRating());
			pstmt.setInt(3, reviewDTO.getRestaurantId());
			pstmt.setInt(4, reviewDTO.getUserId());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// review_id를 매개변수로 받아서 목록의 review번호와 일치하면 삭제 reviewId는 review목록. 사용자 정보화면으로
	// 이동하면 사용자가 작성한 리뷰목록이 나옴, 1,2,3..이렇게 그래서 그 번호를 입력받으면 번호에 해당하는 리뷰 삭제
	// @param id review목록의 번호
	public void deleteReview(int id) {
		String deleteSQL = "DELETE FROM reviews WHERE review_id=?";
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(deleteSQL);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 사용자 화면에서 본인이 작성한 리뷰목록을 보여줌, 1번 리뷰를 선택하면 1번 리뷰를 수정 가능
	// @param content review내용, id review목록의 번호
	public void updateReview(String content, int id) {
		String updateSQL = "UPDATE reviews SET review_content = ? WHERE user_id=?";
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, content);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// database에서 rating을 얻어와야 함, 그 후 점수별로 review를 보여줘야 함
	// @param restaurantId, rating
	// @return 점수별로 review 출력
	// pageSize는 목록의 번호, main class로부터 받아옴
	public ArrayList<ReviewDTO> selectCategorizedRating(int pageSize, int rating, int restaurantId) {
		String selectCategorizedSQL = "SELECT rating, review_content, write_time from reviews where restaurant_id=? and rating =?";
		ArrayList<ReviewDTO> categorizedReviews = new ArrayList<>();
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(selectCategorizedSQL);
			// 1: 첫번쩨 물음표, 순서, 받아온 rating값이 ?에 들어감
			pstmt.setInt(1, restaurantId);
			pstmt.setInt(2, rating);
			// select문의 결과를 저장하는 객체
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO reviewDTO = new ReviewDTO();
				// get으로 가져와서 set으로 dto에 값을 넣어준다
				reviewDTO.setRating(rs.getInt("rating"));
				reviewDTO.setContent(rs.getString("review_content"));
				reviewDTO.setWritingTime(rs.getDate("write_time"));
				categorizedReviews.add(reviewDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return categorizedReviews;
	}

	// restaurant로 이동하면 그 restaurant에 해당하는 review들만 출력
	// @param restaurantId 식당 번호, index 목록번호
	// @return restaurantReviews, 반환값 ArrayList<ReviewDTO>
	public ArrayList<ReviewDTO> selectReviewByRestaurant(int pageSize, int restaurantId, int index) {
		String selectByRestaurantSQL = "SELECT *\r\n" + "FROM (SELECT ROWNUM rn, a.*\r\n" + "          FROM ( \r\n"
				+ "          SELECT user_id, review_content, rating, write_time\r\n" + "          FROM reviews \r\n"
				+ "          WHERE restaurant_id = ?)  a\r\n" + "          )\r\n" + "WHERE rn BETWEEN ? AND ?";
		ArrayList<ReviewDTO> restaurantReviews = new ArrayList<>();
		int sizeA = 1 + pageSize * (index - 1);
		int sizeB = pageSize * index;
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(selectByRestaurantSQL);
			pstmt.setInt(1, restaurantId);
			pstmt.setInt(2, sizeA);
			pstmt.setInt(3, sizeB);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setUserId(rs.getInt("user_id"));
				reviewDTO.setContent(rs.getString("review_content"));
				reviewDTO.setRating(rs.getInt("rating"));
				reviewDTO.setWritingTime(rs.getDate("write_time"));
				restaurantReviews.add(reviewDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return restaurantReviews;
	}

	// 사용자 화면에서 내가 쓴 리뷰들만 출력
	// @param userId
	// @return
	public ArrayList<ReviewDTO> selectReviewByUser(int pageSize, int userId) {
		String selectByUserSQL = "SELECT restaurant_id, review_content, rating, write_time, user_id FROM reviews WHERE user_id = ?";
		ArrayList<ReviewDTO> userReviews = new ArrayList<>();
		try {
			conn = JDBC.connect();
			pstmt = conn.prepareStatement(selectByUserSQL);
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO reviewDTO = new ReviewDTO();
				reviewDTO.setRestaurantId(rs.getInt("restaurant_id"));
				reviewDTO.setContent(rs.getString("review_content"));
				reviewDTO.setRating(rs.getInt("rating"));
				reviewDTO.setWritingTime(rs.getDate("write_time"));
				reviewDTO.setUserId(rs.getInt("user_id"));

				userReviews.add(reviewDTO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return userReviews;
	}
}