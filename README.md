For the Advanced Programming class I designed a discord bot called Bookmate, that works like a virtual library that keeps track of your finished books. The commands implemented for this version are:

![image](https://user-images.githubusercontent.com/58936202/119495124-bb361980-bd6a-11eb-985b-37ef7e87e0f8.png)

In order to keep track of all users that are in the same guils as the Bot, I created the USERS and SERVERS tables, and also USER_SERVERS table for easily retrieving each person from a specific server.  An example of the USER_SERVERS is:

![image](https://user-images.githubusercontent.com/58936202/119496192-ea995600-bd6b-11eb-9222-cae1f589a03e.png)
Those attributes are referencing IDs from previous tables.

I needed also to store each user's bookList, that's independent of the server (for instance, if a user is part of multiple Guilds that have our Discord Bot, he/she will have same books on all servers, and if an addition or removal will be made, it will persist in each guild. And in order to design this feature, I created two additional tables, BOOKS and BOOK_USERS (similarily to the USER_SERVERS presented earlier).

Each time a book is inserted, it will have assigned an unique id if the (title,author) set doesn't match any existing book, and this id will be used to store the list of books per each member.

The existing books from the bot's library are retrieved form wikipedia's RSS feed https://en.wikipedia.org/w/index.php?title=List_of_fantasy_novels_(A%E2%80%93H)&action=history&feed=rss that is constantly updated, and each time a user executes a search command, the internal database is also updating. In order to parse the file, I used ROME 1.0 framework.

The bot monitors each message received, and if the message starts with '->', it will be interpreted as one of it's commands. 
Some of his features include:

- inform members when someone joins the current Guild
![image](https://user-images.githubusercontent.com/58936202/119499400-5b8e3d00-bd6f-11eb-9d11-6883650058b6.png)

- inform members whenever a user is removed from the corresponding Guild
![image](https://user-images.githubusercontent.com/58936202/119499524-7a8ccf00-bd6f-11eb-8126-1d0fe7df3d00.png)

- searches for books retreived from the RSS feed
![image](https://user-images.githubusercontent.com/58936202/119499331-4d402100-bd6f-11eb-90b1-3dcf247a502f.png)

- adds books to a user's finished reading list
![image](https://user-images.githubusercontent.com/58936202/119499621-9001f900-bd6f-11eb-88ab-97b04f66efa2.png)

- provides the list of current user's finished books on request
![image](https://user-images.githubusercontent.com/58936202/119499743-adcf5e00-bd6f-11eb-97fe-ae28ba2de1f6.png)

- removes books from a member's list
![image](https://user-images.githubusercontent.com/58936202/119499809-c5a6e200-bd6f-11eb-841f-373f9a19445e.png)

- other members can check a guild member list of books
![image](https://user-images.githubusercontent.com/58936202/119499900-dce5cf80-bd6f-11eb-830b-994ad7e4d947.png)
