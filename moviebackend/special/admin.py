from django.contrib import admin
from .models import Favorite, Watchlist, RecentlyWatched, RecentlyEpisode, Folder,FolderItem

admin.site.register(Favorite)
admin.site.register(Watchlist)
admin.site.register(RecentlyWatched)
admin.site.register(RecentlyEpisode)
admin.site.register(Folder)
admin.site.register(FolderItem)

