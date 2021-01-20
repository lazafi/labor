# lab 12

bank <- read.csv2("data/bank.csv", stringsAsFactors = TRUE)
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
  ret <- rbind(res, c(NA,NA,error))
  dimnames(ret) <- list(dimnames(res)[[2]], c(dimnames(res)[[1]], 'error')) 
  print(ret, na.print="")
  }

# 1)

install.packages("randomForest")
library(randomForest)
#?randomForest

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
evalu(randomForest(y~., data=bank, subset=c(y_idx, n_idx)))


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

n <- length(train[bank[train,'y'] == "no"])
y <-  length(train[bank[train,'y'] == "yes"])

evalu(randomForest(y~., data=bank, subset=train, cutoff=c(1/y,1/n) ))
evalu(randomForest(y~., data=bank, subset=train, cutoff=c(2/y,1/n) ))
evalu(randomForest(y~., data=bank, subset=train, cutoff=c(3/y,1/n) ))

# e)

evalu(randomForest(y~., data=bank, subset=train, strata=default ))
evalu(randomForest(y~., data=bank, subset=train, strata=y ))
evalu(randomForest(y~., data=bank, subset=train, strata=job ))

# f)

#install.packages("DMwR")
library(DMwR)

stm <- SMOTE(y~., bank[train,])
stm <- SMOTE(y~., bank[train,], perc.over = 300)
table(stm$y)

evalu(randomForest(y~., data=stm))
varImpPlot(randomForest(y~., data=stm, importance=TRUE))





