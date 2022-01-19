package online.nasgar.timedrankup.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Rank {
    private final String name;
    private final String prefix;
    private final int time;
}
