# lab 12

bank <- read.csv2("data/bank.csv")
str(bank)

set.seed(1234)
train <- sample(1:nrow(bank), nrow(bank)*2/3)
test <- (1:nrow(bank)) [-train]

evalu <- function(rf) {
  pred <- predict(rf, bank[test,])
  TAB <- table(bank[test,'y'], pred)
  error <- 1 - sum(diag(TAB)) / sum(TAB)
  ye <- TAB[1,2] / (TAB[1,1] + TAB[1,2])  
  ne <- TAB[2,1] / (TAB[2,1] + TAB[2,2])  

  res <- cbind(TAB, c(ye,ne))

  rbind(res, c(NA,NA,error))
  }

# 1)

library(randomForest)
rf1 <- randomForest(y~., data=bank, subset=train, importance=TRUE)

plot(rf1)
varImpPlot(rf1)

evalu(rf1)


# 2)

# a)

## same-size-sampling
## sample same 'n' as 'y'
y_idx <-train[bank[train,'y'] == "yes"]
n_idx <- sample(train[bank[train,'y'] == "no"], length(y_idx))

rf2_1 <- randomForest(y~., data=bank, subset=c(y_idx, n_idx))

evalu(rf2_1)

# b)

?randomForest

evalu(randomForest(y~., data=bank, subset=train, sampsize=10))
evalu(randomForest(y~., data=bank, subset=train, sampsize=100))
evalu(randomForest(y~., data=bank, subset=train, sampsize=200))
evalu(randomForest(y~., data=bank, subset=train, sampsize=300))
evalu(randomForest(y~., data=bank, subset=train, sampsize=400))
evalu(randomForest(y~., data=bank, subset=train, sampsize=1000))

# c)

evalu(randomForest(y~., data=bank, subset=train, classwt=c(length(n_idx)/length(y_idx),1)))

# d)
