package de.schreib.handball.handballtag.mail

import javax.activation.DataSource
import javax.sql.CommonDataSource

class Attachment(val name:String, val dataSource: DataSource)