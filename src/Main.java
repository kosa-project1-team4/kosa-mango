import favoriteDAO.FavoriteDAO;
import exception.AddException;
import exception.FindException;
import exception.ModifyException;
import exception.RemoveException;
import region.dto.RegionDTO;
import restaurant.dto.RestaurantDTO;
import restaurant.service.RestaurantService;
import review.dao.ReviewDAO;
import review.dto.ReviewDTO;
import user.dao.UserDAO;
import user.dto.UserDTO;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    final static private int PAGE_SIZE = 5;
    static private int userId = Integer.MIN_VALUE;
    public static void main(String[] args) {
        try {
            while (initService());
        } catch (Exception e) {
            System.out.println("오류가 발생하여 프로그램을 종료합니다.");
        }
    }

    /**
     * 사용자가 선택할 메뉴 출력
     * @param menu 메뉴 가변인자
     */
    public static void printMenu(String ...menu) {
        for (int i = 0; i < menu.length; i++) {
            System.out.println(String.format("%d. %s", i+1, menu[i]));
        }
    }

    /**
     * 사용자가 선택한 메뉴 출력
     * @param menu 인덱스가 포함된 메뉴 전체 이름
     */
    public static void printDivide(String menu) {
        final int DIVIDE_REPEAT_COUNT = 100;
        if(menu == null) {
            System.out.println("=".repeat(DIVIDE_REPEAT_COUNT));
        } else {
            int remainDivide = DIVIDE_REPEAT_COUNT - menu.length() - 2;
            System.out.print("=".repeat(remainDivide / 2));
            System.out.print(String.format(" %s ", menu));
            System.out.println("=".repeat(remainDivide / 2));
        }
    }

    /**
     * 서비스 실행 첫 화면
     */
    public static boolean initService() throws FindException, AddException, ModifyException, RemoveException {
        System.out.println("KOSA 플레이트에 방문해주셔서 감사합니다. 무엇을 도와드릴까요?");
        printDivide(null);
        String userChoice = "-1";

        Scanner sc = new Scanner(System.in);
        if (userId == Integer.MIN_VALUE) {
            while(!userChoice.equals("5")) {
                printMenu("식당 검색하기", "식당 추천", "로그인", "회원 가입", "종료하기");
                printDivide(null);

                System.out.print("번호를 입력해주세요: ");
                userChoice = sc.nextLine();

                switch (userChoice) {
                    case "1":
                        searchRestaurant("1. 식당 검색하기");
                        break;
                    case "2":
                        recommendRestaurant("2. 식당 추천");
                        break;
                    case "3":
                        login("3. 로그인");
                        if (userId != Integer.MIN_VALUE) {
                            System.out.println("로그인이 되어 새로고침합니다.");
                            return true;
                        }
                        break;
                    case "4":
                        signup("4. 회원 가입");
                        break;
                    case "5":
                        System.out.println("KOSA 플레이트를 종료합니다.");
                        break;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                }
            }
        } else {
            while(!userChoice.equals("5")) {
                printMenu("식당 검색하기", "식당 추천", "내 정보 보기", "로그아웃", "종료하기");

                System.out.print("번호를 입력해주세요: ");
                userChoice = sc.nextLine();

                switch (userChoice) {
                    case "1":
                        searchRestaurant("1. 식당 검색하기");
                        break;
                    case "2":
                        recommendRestaurant("2. 식당 추천");
                        break;
                    case "3":
                        viewMyInfo("3. 내 정보 보기");
                        if (userId == Integer.MIN_VALUE) {
                            System.out.println("회원 탈퇴되어 새로고침합니다.");
                            return true;
                        }
                        break;
                    case "4":
                        userId = Integer.MIN_VALUE;
                        System.out.println("로그아웃되어 새로고침합니다.");
                        return true;
                    case "5":
                        System.out.println("KOSA 플레이트를 종료합니다.");
                        break;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                }
            }
        }

        printDivide(null);
        System.out.println("감사합니다. 좋은 하루 보내세요^^");
        sc.close();
        return false;
    }

    /**
     * 검색 화면 출력
     */
    public static void searchRestaurant(String menu) throws FindException, AddException {
        printDivide(menu);
        Scanner sc = new Scanner(System.in);
        System.out.print("검색할 지역, 식당 또는 메뉴를 입력해주세요: ");
        String keyword = sc.nextLine();
        System.out.println();
        RestaurantService rService = new RestaurantService(PAGE_SIZE);

        int index = 1;
        int userInput = 0;
        do  {
            int beforeIndex = Integer.MIN_VALUE;
            if (index != 1) {
                System.out.println("0. 이전으로\n");
                beforeIndex = 0;
            }
            rService.printRestaurantList("GENERAL_SEARCH", keyword, index);
            System.out.println();
            if (rService.getRestaurantCount() == 0) {
                System.out.println("검색 결과가 없습니다.");
                printDivide(null);
                break;
            }
            int totalPage = rService.getRestaurantCount() % PAGE_SIZE != 0? rService.getRestaurantCount() / PAGE_SIZE + 1: rService.getRestaurantCount() / PAGE_SIZE;
            int nextIndex = Integer.MIN_VALUE;;
            int quitIndex = Integer.MIN_VALUE;;

            if (index < totalPage) {
                nextIndex = PAGE_SIZE + 1;
                quitIndex = PAGE_SIZE + 2;
                System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
            } else if (index == totalPage) {
                if (rService.getRestaurantCount() % PAGE_SIZE == 0) {
                    quitIndex = PAGE_SIZE + 1;
                    System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                } else {
                    quitIndex = rService.getRestaurantCount() % PAGE_SIZE + 1;
                    System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                }
            }
            printDivide("검색된 식당 개수: " + rService.getRestaurantCount());
            System.out.print("번호를 입력해주세요: ");
            try {
                userInput = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("숫자를 입력해주세요.");
                printDivide(null);
                continue;
            }
            if (userInput == beforeIndex) {
                index--;
            } else if (userInput == nextIndex) {
                index++;
            } else if (userInput == quitIndex) {
                break;
            } else if (userInput <= Math.max(nextIndex, quitIndex) && userInput > 0) {
                viewDetailRestaurant(userInput, rService);
            } else {
                System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                index = 1;
            }
        } while (true);
    }

    public static void viewDetailRestaurant(int restaurantIndex, RestaurantService rService) throws FindException, AddException {
        printDivide("식당 상세 정보");
        RestaurantDTO rDTO= rService.printDetailRestaurant("VIEW_DETAIL", restaurantIndex);
        printDivide(null);
        Scanner sc = new Scanner(System.in);
        int userInput = 0;
        if (userId != Integer.MIN_VALUE) {
            do {
                printMenu("찜 하기", "리뷰 쓰기", "전체 리뷰 보기", "별점 별 리뷰 보기", "이전으로");
                System.out.print("번호를 입력해주세요: ");
                try {
                    userInput = Integer.parseInt(sc.nextLine());
                } catch (Exception e) {
                    System.out.println("숫자를 입력해주세요.");
                    printDivide(null);
                    continue;
                }
                if (userInput == 1) {
                    printDivide("1. 찜 하기");
                    FavoriteDAO fDAO = new FavoriteDAO();
                    try {
                        fDAO.insertFavorites(userId, rDTO.getId());
                        System.out.println(String.format("찜 목록에 \"%s\"가 추가되었습니다.", rDTO.getName()));
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + " 이미 찜 목록에 존재하는 식당입니다.");
                    }
                    break;
                } else if (userInput == 2) {
                    printDivide("2. 리뷰 쓰기 - " + rDTO.getName());
                    ReviewDAO reviewDAO = new ReviewDAO();
                    ReviewDTO reviewDTO = new ReviewDTO();
                    System.out.print("평가를 선택하세요 ");
                    System.out.print("(1. 맛있어요 / 2. 그냥 그래요 / 3. 별로에요): ");
                    int rating;
                    try {
                        rating = Integer.parseInt(sc.nextLine());
                    } catch (Exception e) {
                        System.out.println("숫자를 입력해주세요.");
                        printDivide(null);
                        continue;
                    }
                    if (rating == 1) {
                        reviewDTO.setRating(5);
                    } else if (rating == 2) {
                        reviewDTO.setRating(3);
                    } else {
                        reviewDTO.setRating(1);
                    }

                    System.out.print("리뷰 내용을 한 줄로 입력해주세요: ");
                    String inputContent = sc.nextLine();
                    reviewDTO.setContent(inputContent);

                    reviewDTO.setUserId(userId);
                    reviewDTO.setRestaurantId(rDTO.getId());
                    reviewDAO.insertReview(reviewDTO);
                    break;
                } else if (userInput == 3) {
                    printDivide("3. 전체 리뷰 보기 - " + rDTO.getName());
                    ReviewDAO reviewDAO = new ReviewDAO();
                    int index = 1;
                    int userInputReviewOption;
                    HashMap<Integer, String> scoreMap = new HashMap<>();
                    scoreMap.put(5, "맛있어요");
                    scoreMap.put(3, "그냥 그래요");
                    scoreMap.put(1, "별로에요");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    do  {
                        int beforeIndex = Integer.MIN_VALUE;
                        if (index != 1) {
                            System.out.println("0. 이전으로\n");
                            beforeIndex = 0;
                        }
                        ArrayList<ReviewDTO> reviewList = reviewDAO.selectReviewByRestaurant(PAGE_SIZE, rDTO.getId(), index);
                        for (int i = 0; i < reviewList.size(); i++) {
                            ReviewDTO reviewDTO = reviewList.get(i);
                            System.out.println(i+1 + ".");
                            System.out.println(String.format("%s / %s", scoreMap.get(reviewDTO.getRating()), format.format(reviewDTO.getWritingTime())));
                            System.out.println(reviewDTO.getContent());
                            System.out.println("-".repeat(30));
                        }

                        System.out.println();
                        if (reviewDAO.getReviewCount() == 0) {
                            System.out.println("검색 결과가 없습니다.");
                            break;
                        }
                        int totalPage = reviewDAO.getReviewCount() % PAGE_SIZE != 0? reviewDAO.getReviewCount() / PAGE_SIZE + 1: reviewDAO.getReviewCount() / PAGE_SIZE;
                        int nextIndex = Integer.MIN_VALUE;;
                        int quitIndex = Integer.MIN_VALUE;;

                        if (totalPage == index) {
                            System.out.println("모든 리뷰를 출력하였습니다.");
                            printDivide("검색된 리뷰 개수: " + reviewDAO.getReviewCount());
                            break;
                        }
                        printDivide("검색된 리뷰 개수: " + reviewDAO.getReviewCount());
                        if (index < totalPage) {
                            nextIndex = PAGE_SIZE + 1;
                            quitIndex = PAGE_SIZE + 2;
                            System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                            System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                        } else if (index == totalPage) {
                            if (reviewDAO.getReviewCount() % PAGE_SIZE == 0) {
                                quitIndex = PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            } else {
                                quitIndex = reviewDAO.getReviewCount() % PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            }
                        }
                        System.out.print("이전/다음/종료에 해당하는 번호를 입력해주세요: ");
                        try {
                            userInputReviewOption = Integer.parseInt(sc.nextLine());
                        } catch (Exception e) {
                            System.out.println("숫자를 입력해주세요.");
                            printDivide(null);
                            continue;
                        }
                        if (userInputReviewOption == beforeIndex) {
                            index--;
                        } else if (userInputReviewOption == nextIndex) {
                            index++;
                        } else if (userInputReviewOption == quitIndex) {
                            break;
                        } else {
                            System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                            index = 1;
                        }
                    } while (true);
                    break;
                } else if (userInput == 4) {
                    printDivide("4. 별점 별 리뷰 보기 - " + rDTO.getName());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    System.out.println("확인할 별점을 입력하세요. (1. 맛있어요 / 2. 그냥 그래요 / 3. 별로에요): ");
                    String ratingInput = sc.nextLine();
                    int searchRating = -1;
                    if (ratingInput.equals(("1"))) {
                        searchRating = 5;
                    } else if (ratingInput.equals(("2"))) {
                        searchRating = 3;
                    } else if (ratingInput.equals(("3"))) {
                        searchRating = 1;
                    }

                    ReviewDAO reviewDAO = new ReviewDAO();
                    ArrayList<ReviewDTO> reviewListForRating = reviewDAO.selectCategorizedRating(searchRating, rDTO.getId());
                    if (reviewListForRating.size() == 0) {
                        System.out.println("해당 별점의 리뷰가 없습니다.");
                        break;
                    }
                    for (int i = 0; i < reviewListForRating.size(); i++) {
                        System.out.println(String.format("%s / %s", reviewListForRating.get(i).getContent(), format.format(reviewListForRating.get(i).getWritingTime())));
                    }
                    printDivide("검색된 리뷰 개수: " + reviewDAO.getReviewCount());
                    break;

                } else if(userInput == 5) {
                    break;
                } else {
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                    printDivide(null);
                }
            } while (userInput != 5);

        }
    }
    public static void recommendRestaurant(String menu) throws FindException, AddException {
        printDivide(menu);
        String userRecommandChoice = "-1";
        Scanner sc = new Scanner(System.in);
        System.out.println("번호를 입력해주세요:");
        while(!userRecommandChoice.equals("4")) {
            printMenu("메뉴별 인기 맛집", "지역별 인기 맛집", "오늘 뭐먹지?", "이전으로");

            System.out.print("번호를 입력해주세요: ");
            userRecommandChoice = sc.nextLine();
            RestaurantService rService = new RestaurantService(PAGE_SIZE);

            int index = 1;
            int userInput;
            switch (userRecommandChoice) {
                case "1":
                    printDivide("1. 메뉴별 인기 맛집");
                    Set<String> categorySet = rService.getRankCategoryList().keySet();
                    String[] categoryArray = new String[categorySet.size()];
                    int categoryListIndex = 0;
                    for(String category : categorySet) {
                        System.out.println(String.format("%d. %s", categoryListIndex + 1, category));
                        categoryArray[categoryListIndex] = category;
                        categoryListIndex++;
                    }
                    printDivide(null);
                    System.out.println("확인할 메뉴 번호를 입력해주세요:");
                    String choiceMenu = "";
                    try {
                        choiceMenu = categoryArray[Integer.parseInt(sc.nextLine()) - 1];
                    } catch (Exception e) {
                        System.out.println("숫자를 입력해주세요.");
                        printDivide(null);
                        continue;
                    }
                    printDivide(choiceMenu + " 맛집");

                    index = 1;
                    do  {
                        int beforeIndex = Integer.MIN_VALUE;
                        if (index != 1) {
                            System.out.println("0. 이전으로\n");
                            beforeIndex = 0;
                        }
                        rService.printRestaurantList("RANK_CATEGORY", choiceMenu, index);
                        System.out.println();
                        if (rService.getRestaurantCount() == 0) {
                            System.out.println("검색 결과가 없습니다.");
                            printDivide(null);
                            break;
                        }
                        int totalPage = rService.getRestaurantCount() % PAGE_SIZE != 0? rService.getRestaurantCount() / PAGE_SIZE + 1: rService.getRestaurantCount() / PAGE_SIZE;
                        int nextIndex = Integer.MIN_VALUE;;
                        int quitIndex = Integer.MIN_VALUE;;

                        if (index < totalPage) {
                            nextIndex = PAGE_SIZE + 1;
                            quitIndex = PAGE_SIZE + 2;
                            System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                            System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                        } else if (index == totalPage) {
                            if (rService.getRestaurantCount() % PAGE_SIZE == 0) {
                                quitIndex = PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            } else {
                                quitIndex = rService.getRestaurantCount() % PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            }
                        }
                        printDivide("랭크된 식당 개수: " + rService.getRestaurantCount());
                        System.out.print("상세 정보를 확인할 식당 번호를 입력해주세요: ");
                        try {
                            userInput = Integer.parseInt(sc.nextLine());
                        } catch (Exception e) {
                            System.out.println("숫자를 입력해주세요.");
                            printDivide(null);
                            continue;
                        }
                        if (userInput == beforeIndex) {
                            index--;
                        } else if (userInput == nextIndex) {
                            index++;
                        } else if (userInput == quitIndex) {
                            break;
                        } else if (userInput <= Math.max(nextIndex, quitIndex) && userInput > 0) {
                            viewDetailRestaurant(userInput, rService);
                        } else {
                            System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                            index = 1;
                        }
                    } while (true);
                    break;
                case "2":
                    printDivide("2. 지역별 인기 맛집");
                    HashSet<String> regionSet = new HashSet<>();
                    ArrayList<String> regionList = rService.getRankRegionList();
                    int regionListIndex = 0;
                    for(String region : regionList) {
                        if (!regionSet.contains(region)) {
                            System.out.println(String.format("%d. %s", regionListIndex + 1, region));
                            regionListIndex++;
                            regionSet.add(region);
                        }

                    }

                    printDivide(null);
                    System.out.print("확인할 메뉴 번호를 입력해주세요: ");
                    String choiceRegion = "";
                    try {
                        choiceRegion = regionList.get(Integer.parseInt(sc.nextLine()) - 1);
                    } catch(Exception e) {
                        System.out.println("숫자를 입력해주세요.");
                        printDivide(null);
                        continue;
                    }
                    printDivide(choiceRegion + " 맛집");

                    index = 1;
                    do  {
                        int beforeIndex = Integer.MIN_VALUE;
                        if (index != 1) {
                            System.out.println("0. 이전으로\n");
                            beforeIndex = 0;
                        }
                        rService.printRestaurantList("RANK_REGION", choiceRegion, index);
                        System.out.println();
                        if (rService.getRestaurantCount() == 0) {
                            System.out.println("검색 결과가 없습니다.");
                            printDivide(null);
                            break;
                        }
                        int totalPage = rService.getRestaurantCount() % PAGE_SIZE != 0? rService.getRestaurantCount() / PAGE_SIZE + 1: rService.getRestaurantCount() / PAGE_SIZE;
                        int nextIndex = Integer.MIN_VALUE;;
                        int quitIndex = Integer.MIN_VALUE;;

                        if (index < totalPage) {
                            nextIndex = PAGE_SIZE + 1;
                            quitIndex = PAGE_SIZE + 2;
                            System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                            System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                        } else if (index == totalPage) {
                            if (rService.getRestaurantCount() % PAGE_SIZE == 0) {
                                quitIndex = PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            } else {
                                quitIndex = rService.getRestaurantCount() % PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            }
                        }
                        printDivide("랭크된 식당 개수: " + rService.getRestaurantCount());
                        System.out.print("상세 정보를 확인할 식당 번호를 입력해주세요: ");
                        try {
                            userInput = Integer.parseInt(sc.nextLine());
                        } catch (Exception e) {
                            System.out.println("숫자를 입력해주세요.");
                            printDivide(null);
                            continue;
                        }
                        if (userInput == beforeIndex) {
                            index--;
                        } else if (userInput == nextIndex) {
                            index++;
                        } else if (userInput == quitIndex) {
                            break;
                        } else if (userInput <= Math.max(nextIndex, quitIndex) && userInput > 0) {
                            viewDetailRestaurant(userInput, rService);
                        } else {
                            System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                            index = 1;
                        }
                    } while (true);
                    break;
                case "3":
                    printDivide("3. 오늘 뭐먹지? (내 집 주변 랜덤 식당)");
                    if (userId != Integer.MIN_VALUE) {
                        index = 1;
                        try {
                            rService.printDetailRestaurant("VIEW_RANDOM", userId);
                        } catch (Exception e) {
                            System.out.println("올바른 주소가 아닙니다.");
                        }
                        printDivide(null);
                    } else {
                        System.out.println("로그인 후 이용해주세요.");
                    }
                    break;
                case "4":
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }
    public static void login(String menu) throws FindException {
        printDivide(menu);

        Scanner sc = new Scanner(System.in);
        UserDAO uDAO = new UserDAO();
        UserDTO loginUser = new UserDTO();

        System.out.print("아이디를 입력하세요: ");
        loginUser.setUserId(sc.nextLine());
        System.out.print("비밀번호를 입력하세요: ");
        loginUser.setPassword(sc.nextLine());

        loginUser = uDAO.login(loginUser);
        if (loginUser == null) {
            System.out.println("로그인에 실패했습니다. 아이디 혹은 비밀번호를 다시 확인해주세요.");
            return;
        }
        userId = loginUser.getId();

        printDivide(null);
    }
    public static void signup(String menu) throws FindException, AddException {
        printDivide(menu);
        Scanner sc = new Scanner(System.in);
        UserDTO signupUser = new UserDTO();
        System.out.print("아이디를 입력해주세요: ");
        signupUser.setUserId(sc.nextLine());
        System.out.print("비밀번호를 입력해주세요: ");
        signupUser.setPassword(sc.nextLine());
        System.out.print("이름을 입력해주세요: ");
        signupUser.setUserName(sc.nextLine());
        System.out.print("성별을 입력해주세요 (남/여): ");
        while(true) {
            String inputGender = sc.nextLine();
            if (inputGender.equals("남")) {
                signupUser.setGender(1);
                break;
            } else if (inputGender.equals("여")) {
                signupUser.setGender(2);
                break;
            } else {
                System.out.println("잘못된 성별입니다. 다시 입력하세요.");
            }
        }

        RegionDTO inputRegion = new RegionDTO();
        System.out.print("주소(시/도 시/군/구 동/읍/면)를 입력해주세요 (ex. 서울시 송파구 가락동 ): ");
        while(true) {
            String inputAddress = sc.nextLine();
            String[] addressList = inputAddress.split(" ");
            if (addressList.length == 3) {
                inputRegion.setCityName(addressList[0]);
                inputRegion.setSiGunGu(addressList[1]);
                inputRegion.setDongEupMyeon(addressList[2]);
                break;
            } else {
                System.out.println("주소가 올바르게 입력되지 않았습니다. 다시 입력하세요.");
            }
        }
        System.out.print("우편 번호를 입력하세요: ");
        inputRegion.setZipcode(sc.nextLine());
        signupUser.setRegion(inputRegion);

        UserDAO userDAO = new UserDAO();
        userDAO.join(signupUser);
        printDivide(null);
    }
    public static void viewMyInfo(String menu) throws FindException, AddException, ModifyException, RemoveException {
        printDivide(menu);
        String userChoice = "-1";
        Scanner sc = new Scanner(System.in);
        System.out.println("번호를 입력해주세요:");

        UserDAO uDAO = new UserDAO();
        ReviewDAO rDAO = new ReviewDAO();
        int index = 1;

        while(!userChoice.equals("6")) {
            printMenu("내 찜 목록 조회 및 삭제", "내가 쓴 리뷰 수정", "내가 쓴 리뷰 삭제", "비밀번호 수정", "회원 탈퇴", "이전으로");

            System.out.print("번호를 입력해주세요: ");
            userChoice = sc.nextLine();

            int userInputReviewOption;
            HashMap<Integer, String> scoreMap = new HashMap<>();
            scoreMap.put(5, "맛있어요");
            scoreMap.put(3, "그냥 그래요");
            scoreMap.put(1, "별로에요");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            switch (userChoice) {
                case "1":
                    manageFavorite("1. 내 찜 목록 조회 및 삭제");
                    break;
                case "2":
                    printDivide("2. 내가 쓴 리뷰 수정");
                    index = 1;
                    do  {
                        int beforeIndex = Integer.MIN_VALUE;
                        if (index != 1) {
                            System.out.println("0. 이전으로\n");
                            beforeIndex = 0;
                        }
                        ArrayList<ReviewDTO> reviewList = rDAO.selectReviewByUser(PAGE_SIZE, userId, index);
                        for (int i = 0; i < reviewList.size(); i++) {
                            ReviewDTO reviewDTO = reviewList.get(i);
                            System.out.println(i+1 + ".");
                            System.out.println(String.format("%s / %s", scoreMap.get(reviewDTO.getRating()), format.format(reviewDTO.getWritingTime())));
                            System.out.println(reviewDTO.getContent());
                            System.out.println("-".repeat(30));
                        }

                        System.out.println();
                        if (rDAO.getReviewCount() == 0) {
                            System.out.println("검색 결과가 없습니다.");
                            break;
                        }
                        int totalPage = rDAO.getReviewCount() % PAGE_SIZE != 0? rDAO.getReviewCount() / PAGE_SIZE + 1: rDAO.getReviewCount() / PAGE_SIZE;
                        int nextIndex = Integer.MIN_VALUE;;
                        int quitIndex = Integer.MIN_VALUE;;

                        if (index < totalPage) {
                            nextIndex = PAGE_SIZE + 1;
                            quitIndex = PAGE_SIZE + 2;
                            System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                            System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                        } else if (index == totalPage) {
                            if (rDAO.getReviewCount()  % PAGE_SIZE == 0) {
                                quitIndex = PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            } else {
                                quitIndex = rDAO.getReviewCount() % PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            }
                        }
                        printDivide("검색된 리뷰 개수: " + rDAO.getReviewCount());
                        System.out.print("수정할 리뷰 번호를 입력해주세요: ");
                        try{
                            userInputReviewOption = Integer.parseInt(sc.nextLine());
                        } catch (Exception e) {
                            System.out.println("숫자를 입력해주세요.");
                            printDivide(null);
                            continue;
                        }
                        if (userInputReviewOption == beforeIndex) {
                            index--;
                        } else if (userInputReviewOption == nextIndex) {
                            index++;
                        } else if (userInputReviewOption == quitIndex) {
                            break;
                        } else if (userInputReviewOption > 0 && userInputReviewOption < Math.max(nextIndex, quitIndex)) {
                            printDivide(userInputReviewOption + "번 리뷰 수정");
                            System.out.print("수정할 리뷰 내용을 입력하세요: ");
                            String content = sc.nextLine();
                            System.out.print("수정할 평점을 입력하세요: ");
                            int rating = 0;
                            try {
                                rating = Integer.parseInt(sc.nextLine());
                            } catch (Exception e) {
                                System.out.println("숫자를 입력해주세요.");
                                printDivide(null);
                                continue;
                            }
                            if (rating == 1 || rating == 3 || rating == 5) {
                                rDAO.updateReview(content, rating, reviewList.get(userInputReviewOption - 1).getId());
                            } else {
                                System.out.println("올바른 평점이 아닙니다. 리뷰가 수정되지 않습니다.");
                            }
                            printDivide(null);
                            break;
                        } else {
                            System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                            index = 1;
                        }
                    } while (true);
                    break;
                case "3":
                    printDivide("3. 내가 쓴 리뷰 삭제");
                    index = 1;
                    do  {
                        int beforeIndex = Integer.MIN_VALUE;
                        if (index != 1) {
                            System.out.println("0. 이전으로\n");
                            beforeIndex = 0;
                        }
                        ArrayList<ReviewDTO> reviewList = rDAO.selectReviewByUser(PAGE_SIZE, userId, index);
                        for (int i = 0; i < reviewList.size(); i++) {
                            ReviewDTO reviewDTO = reviewList.get(i);
                            System.out.println(i+1 + ".");
                            System.out.println(String.format("%s / %s", scoreMap.get(reviewDTO.getRating()), format.format(reviewDTO.getWritingTime())));
                            System.out.println(reviewDTO.getContent());
                            System.out.println("-".repeat(30));
                        }

                        System.out.println();
                        if (rDAO.getReviewCount() == 0) {
                            System.out.println("검색 결과가 없습니다.");
                            break;
                        }
                        int totalPage = rDAO.getReviewCount() % PAGE_SIZE != 0? rDAO.getReviewCount() / PAGE_SIZE + 1: rDAO.getReviewCount() / PAGE_SIZE;
                        int nextIndex = Integer.MIN_VALUE;;
                        int quitIndex = Integer.MIN_VALUE;;

                        if (index < totalPage) {
                            nextIndex = PAGE_SIZE + 1;
                            quitIndex = PAGE_SIZE + 2;
                            System.out.println(String.format("%d. %s", nextIndex, "다음으로"));
                            System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                        } else if (index == totalPage) {
                            if (rDAO.getReviewCount()  % PAGE_SIZE == 0) {
                                quitIndex = PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            } else {
                                quitIndex = rDAO.getReviewCount() % PAGE_SIZE + 1;
                                System.out.println(String.format("%d. %s", quitIndex, "종료하기"));
                            }
                        }
                        printDivide("검색된 리뷰 개수: " + rDAO.getReviewCount());
                        System.out.print("삭제할 리뷰 번호를 입력해주세요: ");
                        try {
                            userInputReviewOption = Integer.parseInt(sc.nextLine());
                        } catch (Exception e) {
                            System.out.println("숫자를 입력해주세요.");
                            printDivide(null);
                            continue;
                        }
                        if (userInputReviewOption == beforeIndex) {
                            index--;
                        } else if (userInputReviewOption == nextIndex) {
                            index++;
                        } else if (userInputReviewOption == quitIndex) {
                            break;
                        } else if (userInputReviewOption > 0 && userInputReviewOption < Math.max(nextIndex, quitIndex)) {
                            printDivide(userInputReviewOption + "번 리뷰 삭제");
                            rDAO.deleteReview(reviewList.get(userInputReviewOption - 1).getId());
                            System.out.println(reviewList.get(userInputReviewOption - 1).getId());
                            System.out.println("삭제 되었습니다.");
                            printDivide(null);
                            break;
                        } else {
                            System.out.println("잘못된 입력입니다. 처음 검색 결과로 돌아갑니다.");
                            index = 1;
                        }
                    } while (true);
                    break;
                case "4":
                    printDivide("4. 비밀번호 수정");
                    System.out.print("수정할 비밀번호를 입력하세요: ");
                    String password = sc.nextLine();
                    try {
                        uDAO.updateUser(userId, password);
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "다시 시도해주세요.");
                    }
                    printDivide(null);
                    break;
                case "5":
                    printDivide("5. 회원 탈퇴");
                    uDAO.deleteUser(userId);
                    userId = Integer.MIN_VALUE;
                    printDivide(null);
                    return;
                case "6":
                    printDivide(null);
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }


    public static void manageFavorite(String menu) throws FindException, AddException {
        printDivide(menu);
        FavoriteDAO fDAO = new FavoriteDAO();
        ArrayList<RestaurantDTO> restaurantList = fDAO.selectFavoritesByUserId(userId);
        if (restaurantList.size() == 0) {
            System.out.println("찜한 식당이 없습니다.");
            printDivide(null);
            return;
        }
        for (int i = 0; i < restaurantList.size(); i++) {
            System.out.println(String.format("%d. %s", i+1, restaurantList.get(i).getName()));
        }
        System.out.println("0. 이전으로");
        while (true) {
            printDivide(null);
            System.out.println("삭제할 찜 목록 번호를 입력하세요: ");
            Scanner sc = new Scanner(System.in);
            int inputIndex = 0;
            try {
                inputIndex = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("숫자를 입력해주세요.");
                printDivide(null);
                continue;
            }
            if (inputIndex == 0) {
                break;
            } else if (inputIndex <= restaurantList.size()) {
                fDAO.deleteFavorites(userId, restaurantList.get(inputIndex-1).getId());
                break;
            } else {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
    }

}