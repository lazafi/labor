#lab8

#1)

#install.packages("rrcov")
data(olitos, package="rrcov")

str(olitos)
summary(olitos)

#a)

olitos.a <- olitos[which(olitos$grp %in% c(1,3)), -26]
grp <- olitos[which(olitos$grp %in% c(1,3)), "grp"]
y <- ifelse(grp==1, 1, 0)
olitos.a <- cbind(olitos.a, y)

set.seed(1234)
n <- nrow(olitos.a)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]


modelglm <- glm(y~X1+X2+X3+X4+X5+X6,data=olitos.a, family=binomial, subset=train)
modelglm
summary(modelglm)
#plot(modelglm)

#b)

pred.a <- predict(modelglm, olitos.a[test,], type="response")
plot(pred.a, col=as.numeric(olitos.a[test,"y"]+1))
abline(h=0.5)
T <- table(olitos.a[test,"y"], pred.a>0.5)
T
e1 <- 1-sum(diag(T))/sum(T)
e1

pred.a2 <- predict(modelglm, olitos.a[test,], type="response")
pred.a2
plot(pred.a2, col=as.numeric(olitos.a[test,"y"]+1))
abline(h=0.5)
table(pred.a2>0.5, olitos.a[test,"y"])

## c)

modelglm.c <- glm(y~.,data=olitos.a, family=binomial, subset=train)
modelglm.c
summary(modelglm.c)

pred.c <- predict(modelglm.c, olitos.a[test,], type="link")
plot(pred.c, col=as.numeric(olitos.a[test,"y"]+1))
abline(h=0)
T <- table(olitos.a[test,"y"], pred.c>0)
T
e2 <- 1-sum(diag(T))/sum(T)
e2

modelglm.step <- step(modelglm.c)

summary(modelglm.step)

pred.step <- predict(modelglm.step, olitos.c[test,], type="link")
plot(pred.step, col=as.numeric(olitos.c[test,"y"]+1))
abline(h=0)
T <- table(olitos.c[test,"y"], pred.step>0)
T
e2 <- 1-sum(diag(T))/sum(T)
e2

##lab: warning becouse we have sigularity issue: less observations in one group (20) then variables (25). 
## solution: select fewer vars

table(olitos.a[train,"y"])
# 2)

#install.packages("VGAM")
library(VGAM)
?vglm

modelvglm <- vglm(grp~X1+X2+X3+X4+X5+X6,data=olitos, ,family="multinomial", subset=train)
summary(modelvglm)
#plot(modelvglm)
pred.2 <- predict(modelvglm, olitos[test,], type="link")
plot(pred.2, col=as.numeric(olitos[test,"grp"]))

##b)
T <- table(olitos[test,"grp"], apply(pred.2, 1, which.max))
T
e1 <- 1-sum(diag(T))/sum(T)
e1

## 3)

library(glmnet)
modelvglm <- cv.glmnet(grp~.,data=olitos, ,family="multinomial", subset=train)
summary(modelvglm)
plot(modelvglm)
pred.3 <- predict(modelvglm, olitos[test,], type="link")
plot(pred.3, col=as.numeric(olitos[test,"grp"]))

##b)
T <- table(olitos[test,"grp"], apply(pred.3, 1, which.max))
T
e1 <- 1-sum(diag(T))/sum(T)
e1


# 4)
bank <- read.csv2("data/bank.csv")
#y <- ifelse(bank$y=="yes", 1, 0)
#bank$y = y

set.seed(1234)
train <- sample(1:nrow(bank), 3000)
test <- (1:nrow(bank)) [-train]


modelglm.4 <- glm(y~.,data=bank, family=binomial, subset=train)
modelglm.4
summary(modelglm.4)


pred.4 <- predict(modelglm.4, bank[test,], type="link")
plot(pred.4, col=as.numeric(bank[test, "y"])+1)
abline(h=0)
abline(h=-2.5)

T <- table(bank[test,"y"], pred.4>0)
T


T <- table(bank[test,"y"], pred.4>-2.5)
T



weights = seq(1,16)
weights
length(weights)
length(bank[,-17])
w <- bank$
  
modelglm.4b <- glm(formula=y~., data=bank, family=binomial, subset=train, weights=seq(1,nrow(bank[train,])))


## lab observations are weighted!

weight[bank$y[train]==1] <- w1
weight[bank$y[train]==0] <- w2


