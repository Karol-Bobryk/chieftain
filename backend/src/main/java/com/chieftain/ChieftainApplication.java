package com.chieftain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// code ( kot )
//        /|_ /|
//     \ / _  _ \ /
//     -<    ^   >-
//     /  \    /  \
//        /    \
//       /      |
//       |      |
//  |\__/        \
//   \______\_\\__\

@SpringBootApplication
@EnableAsync
public class ChieftainApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChieftainApplication.class, args);
  }
}
