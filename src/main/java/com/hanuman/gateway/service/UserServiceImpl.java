package com.hanuman.gateway.service;

import com.hanuman.gateway.dto.UserDto;
import com.hanuman.gateway.service.client.UsersClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class UserServiceImpl {

    static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsersClient usersClient;

    UserServiceImpl(UsersClient usersClient) {
        this.usersClient = usersClient;
    }

    public Mono<List<UserDto>> getUsersData() {
        Mono<List<UserDto>> userDtoList = usersClient.findAllUsers();
        userDtoList = userDtoList.map(users ->
                users.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
        // userDtoList = filterUserListEnds(userDtoList);
        //filterData(userDtoList);
        filterDataUsingMapAndAggregations(userDtoList);
        return userDtoList;
    }

    // Find all users whose website ends with .org and collect them into a List.
    public static Mono<List<UserDto>> filterUserListEnds(Mono<List<UserDto>> userDtoList) {
        return userDtoList
                .map(users ->
                        users.stream()
                                .filter(user -> user.website().endsWith(".org"))
                                .collect(Collectors.toList()));
    }

    // Get a Set of all unique cities where the users live.
    public static void filterData(Mono<List<UserDto>> userDtoList) {
        // 1. Find all users whose website ends with .org and collect them into a List.
        userDtoList
                .map(users ->
                        users.stream()
                                .filter(user -> user.website().endsWith(".org"))
                                .collect(Collectors.toList()))
                .subscribe(userDtos -> log.info("Users whose website ends with .org {}", userDtos));

        // 2. Get a Set of all unique cities where the users live.
        userDtoList
                .map(users -> users.stream()
                        .map(userDto -> userDto.address().city())
                        .collect(Collectors.toSet()))
                .subscribe(cities -> log.info("Unique cities from the usersLive {}", cities));

        // 3. Find the first user who lives in a specific city using findFirst().
        userDtoList
                .map(users -> users.stream()
                        .filter(user -> "Gwenborough".equalsIgnoreCase(user.address().city()))
                        .findFirst())
                .subscribe(userOpt -> log.info("User living in Gwenborough : {}", userOpt.orElse(null)));

        // 4. Check if there is any user in the dataset whose company.name contains the word "Crona".
        // Note: Use .anyMatch() instead of findFirst() to return a clean Boolean as requested by the prompt!
        userDtoList
                .map(users -> users.stream()
                        .anyMatch(user -> user.company().name().contains("Crona")))
                .subscribe(hasCrona -> log.info("Dataset whose company.name word Crona : {}", hasCrona));

        // Extract a list of all user email addresses as lowercase strings.
        userDtoList.map(users -> users
                        .stream()
                        .map(user -> user.email().toLowerCase()).collect(Collectors.toList()))
                .subscribe(emails -> log.info("Users email after converted to lowerCase {}", emails));

        // Transform the user data into a simplified format containing only the user’s name and their phone number
        // (e.g., map User to a custom DTO or string summary).

        userDtoList.map(userDtos ->
                        userDtos.stream()
                                .map(user -> user.name() + ": " + user.phone()))
                .subscribe(summaries -> log.info("User summaries: {}", summaries));

        // Extract a list of all lat and lng coordinates combined together as a single
        // flat list of geographical string values using flatMap.
        userDtoList.map(userDtos ->
                        userDtos.stream()
                                .flatMap(user -> Stream.of(user.address().geo().lat(),
                                        user.address().geo().lng()))
                                .collect(Collectors.toList()))
                .subscribe(coordinates -> log.info("Flat list of all coordinates: {}", coordinates));

        // 📊 Grouping & Partitioning (groupingBy)
        // • Group all users by the city they live in, producing a Map<String, List<User>>.
        userDtoList.map(userDtos -> userDtos
                        .stream()
                        .collect(Collectors.groupingBy(user -> user.address().city(), Collectors.toList())))
                .subscribe(stringListMap -> log.info("City and their userList using grouping : {}", stringListMap));

        // • Partition the users into two groups:
        // those whose id is an even number and those whose id is an odd number using Collectors.partitioningBy.

        userDtoList.map(userDtos -> userDtos
                        .stream()
                        .collect(Collectors.partitioningBy(userDto -> userDto.id() % 2 == 0, Collectors.toList())))
                .subscribe(booleanListMap -> log.info("Into two groups {}", booleanListMap));

        // • Count the total number of users living in each unique city, returning a Map<String, Long>.
        userDtoList.map(userDtos -> userDtos.stream()
                .collect(Collectors.groupingBy(user -> user.address().city(), Collectors.counting()))
        ).subscribe(cityMapAndCount -> log.info("Users living in each unique city and count {}", cityMapAndCount));

    }

    public static void filterDataUsingMapAndAggregations(Mono<List<UserDto>> userList) {
        // 📉 Reductions & Aggregations
        //• Find the user with the highest id value using the max() operator.
        //• Concatenate all user username values into a single comma-separated string (e.g., "Bret, Antonette, Samantha...") using Collectors.joining.
        //• Use the reduce() method to calculate the total sum of all user id fields combined.

        userList.map(userDtos -> userDtos
                        .stream()
                        .max(Comparator.comparing(UserDto::id))
                        .stream().findFirst())
                .subscribe(userHavingLongId -> log.info("User with the highest id value {}", userHavingLongId));

        userList.map(userDtos -> userDtos
                        .stream()
                        .map(UserDto::username)
                        .collect(Collectors.joining(", "))
                )
                .subscribe(userNamesList -> log.info("Username values into a single comma-separated string {}", userNamesList));

        userList.map(userDtos -> userDtos
                .stream()
                .map(UserDto::id)
                .reduce(Long::sum)
        ).subscribe(userIdList -> log.info("Calculate the total sum of all user id fields combined {}", userIdList));

        /*
        🗺️ Advanced Mapping & Grouping (Grouping Map)
    • Group users by their company name, but instead of collecting the full user objects,
    collect only a list of their email addresses (Map<String, List<String>>) using Collectors.mapping.
    • Group the users by city and find the user with the lowest id in each city (Map<String, Optional<User>>).
    • Group the users by city, and map the result to show the company catchphrase of the first user encountered in each city.
         */

        userList.map(userDtos -> userDtos
                .stream()
                .collect(Collectors.groupingBy(userDto -> userDto.company().name(),
                        Collectors.mapping(UserDto::email, Collectors.toList())))
        ).subscribe(companyNameAndTheirEmailMapping -> log.info("Company name and list of their email addresses {}", companyNameAndTheirEmailMapping));


        userList.map(userDtos -> userDtos.stream()
                .collect(Collectors.groupingBy(user -> user.address().city(),
                        Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(UserDto::id)),
                                optional -> optional.orElse(null)
                        )))
        ).subscribe(stringOptionalMap -> log.info("Group the users by city and find the user with the lowest id {}", stringOptionalMap));

        userList.map(userDtos -> userDtos.stream()
                .collect(Collectors.groupingBy(
                        user -> user.address().city(),
                        Collectors.mapping(
                                user -> user.company().catchPhrase(),
                                // Always pick the first catchphrase encountered in that city
                                Collectors.reducing("", (first, second) -> first.isEmpty() ? second : first)
                        )
                ))
        ).subscribe(cityCatchphraseMap -> log.info("City to first catchphrase map: {}", cityCatchphraseMap));

    }

    /*

🔍 Filtering & Basic Collections

• Find all users whose website ends with .org and collect them into a List.
• Get a Set of all unique cities where the users live.
• Find the first user who lives in a specific city (e.g., "Gwenborough") using findFirst().
• Check if there is any user in the dataset whose company.name contains the word "Crona".

🔄 Mapping & Transformations

• Extract a list of all user email addresses as lowercase strings.
• Transform the user data into a simplified format containing only the user’s name and their phone number (e.g., map User to a custom DTO or string summary).
• Extract a list of all lat and lng coordinates combined together as a single flat list of geographical string values using flatMap.

📊 Grouping & Partitioning (groupingBy)

• Group all users by the city they live in, producing a Map<String, List<User>>.
• Partition the users into two groups: those whose id is an even number and those whose id is an odd number using Collectors.partitioningBy.
• Count the total number of users living in each unique city, returning a Map<String, Long>.

📉 Reductions & Aggregations

• Find the user with the highest id value using the max() operator.
• Concatenate all user username values into a single comma-separated string (e.g., "Bret, Antonette, Samantha...") using Collectors.joining.
• Use the reduce() method to calculate the total sum of all user id fields combined.

🗺️ Advanced Mapping & Grouping (Grouping Map)

• Group users by their company name, but instead of collecting the full user objects,
  collect only a list of their email addresses (Map<String, List<String>>) using Collectors.mapping.
• Group the users by city and find the user with the lowest id in each city (Map<String, Optional<User>>).
• Group the users by city, and map the result to show the company catchphrase of the first user encountered in each city.
     */

}
