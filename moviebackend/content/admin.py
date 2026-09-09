from django.contrib import admin
from .models import *


admin.site.register(Movie)
admin.site.register(Series)
admin.site.register(Season)
admin.site.register(Episode)
admin.site.register(Comment)
admin.site.register(Rating)
admin.site.register(StreamMovieLink)
admin.site.register(StreamEpisodeLink)
admin.site.register(Role)
